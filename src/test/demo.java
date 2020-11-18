/**
 * org.ks.drools.test.DroolsEngine.java
 * date:2018年3月12日 上午10:57:49
 */
package com.ultrapower.sigmam.storm.fault.reovery.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;





/**
 * @author patrol
 * date:2018年3月12日 上午10:57:49
 * DroolsEngine 规则引擎
 */
public class DroolsEngine {
	private static Logger log=LoggerFactory.getLogger(DroolsEngine.class);
	private KieServices kieServices;
	//private KieModule kieModule;
	private KieRepository kieRepository;
	private KieFileSystem kieFileSystem;
	private KieContainer kieContainer;
	private KieBase kieBase;
	private ReleaseId releaseId;
	private KieSession kieSession;
	private String engineName;
	
	private RecoveryRuleParser parser;
	
	/**
	 * 2018年10月18日 下午2:03:18
	 * @build:boolean
	 * 最新规则是否已经被编译
	 */
	private volatile boolean build=false;
	
	private final ConcurrentHashMap<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();
	
	private static class  DroolsEngineHolder{
		
		private static final DroolsEngine droolsEngine = new DroolsEngine();
	}
	
	public static DroolsEngine getInstance(){
		return DroolsEngineHolder.droolsEngine;
	}
	
	public DroolsEngine() {
		parser = RecoveryRuleParser.getInstance();
		kieServices = KieServices.get();
	}
	/**
	 * date:2018年10月18日 下午1:54:23
	 * 构建规则引擎
	 */
	public DroolsEngine(String engineName) {
		this();
		this.engineName=engineName;
		//初始化编译规则
		//初始化创建session
		initEngineBase(DroolsEngineConst.KJAR_GROUPID,DroolsEngineConst.KJAR_ARTIFACTID,DroolsEngineConst.KJAR_VERSION).createKieFileSystem(null);
	}
	
	public DroolsEngine initEngineBase(String groupid,String actifactid,String version) {
		long start=System.currentTimeMillis();
		kieServices=KieServices.Factory.get();
		releaseId=kieServices.newReleaseId(groupid, actifactid, version);
		kieRepository=kieServices.getRepository();
		kieFileSystem = kieServices.newKieFileSystem();
		log.info("初始化dools引擎耗时(毫秒)="+ (System.currentTimeMillis()-start));
		return this;
	}
	
	/**
	 * 2018年10月18日 下午1:55:15
	 * @param config
	 * @return DroolsEngine
	 * 
	 */
	/**
	 * 2018年10月18日 下午1:56:32
	 * @param config
	 * @return DroolsEngine
	 * 构建规则引擎
	 */
	public  DroolsEngine createKieFileSystem(JSONObject config) {
		KieModuleModel model=kieServices.newKieModuleModel();
		KieBaseModel basemodel=model.newKieBaseModel(DroolsEngineConst.KIE_BASEMODEL_NAME)
				.setDefault(true)
				.setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);
		basemodel.newKieSessionModel(DroolsEngineConst.KIE_STATELESS_SESSION_NAME)
			.setType(KieSessionType.STATELESS)
			.setDefault(false);
		basemodel.newKieSessionModel(DroolsEngineConst.KIE_STATEFULL_SESSION_NAME)
			.setType(KieSessionType.STATEFUL)
			.setDefault(true);
//		KieBaseModel baseModel = model.newKieBaseModel("FileSystemKBase").addPackage("rules");
//		baseModel.newKieSessionModel("FileSystemKSession");
		kieFileSystem
			.writeKModuleXML(model.toXML())
			.generateAndWritePomXML(releaseId);
		return this;
	}
	
	/**
	 * 2018年9月5日 上午10:48:45
	 * @param rules json数组或包含drlname,drlcontent的json对象，推荐格式[{"drlname":"testrule","drlscripts":""}]
	 * @return DroolsEngine
	 * @throws Exception 
	 * 添加规则到workingMemory，添加之前先删除同名的规则
	 * kieFileSystem添加规则路径 src/main/resources/drlname
	 */
	public DroolsEngine addRules2Engine(JSONArray rules) throws Exception{
		if (rules.size()>0&&build) {
			build=false;
		}
		for (Object o : rules) {
			String drlname=null,drlcontent=null;
			if (o instanceof JSONObject) {
				JSONObject rule=(JSONObject)o;
				drlname=rule.getString(DroolsEngineConst.DRL_NAME_KEY);
				drlcontent=rule.getString(DroolsEngineConst.DRL_CONTENT_KEY);
			}else {
				throw new IllegalArgumentException("do not support "+o.getClass().getName()+" used for rule source.");
			}
			String kieFilePath = DroolsEngineConst.DRL_FILE_WRITEPATH + drlname;
			kieFileSystem.delete(kieFilePath);
			kieFileSystem.write(kieFilePath, drlcontent);
		}
		return this;
	}
	
	public void load(List<AlertRecoveryRule> rules) {
		log.info("[load()]加载规则ing");
		try {
			long start=System.currentTimeMillis();
			//KieServices kieServices = KieServices.get();
			KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
			KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(DroolsEngineConst.KIE_BASEMODEL_NAME);
			kieBaseModel.setDefault(true)
				.setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);
			kieBaseModel.newKieSessionModel(DroolsEngineConst.KIE_STATELESS_SESSION_NAME)
				.setType(KieSessionType.STATELESS)
				.setDefault(false);
			kieBaseModel.newKieSessionModel(DroolsEngineConst.KIE_STATEFULL_SESSION_NAME)
				.setType(KieSessionType.STATEFUL)
				.setDefault(true);
			KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
			List<String> drlNames = new ArrayList<String>();
			List<String> drlContent = new ArrayList<String>();
			for(AlertRecoveryRule ruleInfo : rules){
				if(ruleInfo != null && ruleInfo.getIsactive()== 0 && ruleInfo.getRuleid()!= 0 ){
					String ruleName = String.valueOf(ruleInfo.getRuleid()) + DroolsEngineConst.SCRIPT_RULE_NAME_SUFFIX;
					String path = DroolsEngineConst.DRL_FILE_WRITEPATH + ruleName;
					String ruleContent = parser.parseFromBean(ruleInfo);
					if(ruleContent!=null && !"".equals(ruleContent)){
						kieFileSystem.write(path,ruleContent);
						drlNames.add(ruleName);
						drlContent.add(ruleContent);
						log.info("解析规则为有效的drl文件,ruleName={}",ruleName);
					}
				}
			}
			log.info("解析规则为有效的drl文件数量="+drlNames.size()+",drl文件内容="+drlContent.toString());
			kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
			KieBuilder kieBuilder=kieServices.newKieBuilder(kieFileSystem);
			kieBuilder.buildAll();
			KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
			if(null == kieContainer){
				log.error("kieContainer为空");
				throw new RuntimeException("kieContainer为空:\n" );
			}
			kieContainerMap.put("recoveryRule", kieContainer);
			
			log.info("加载规则完毕,耗时(毫秒)= "+(System.currentTimeMillis()-start));
			String res_str = kieBuilder.getResults().toString();//包含构建信息
			if(kieBuilder.getResults().hasMessages(Level.ERROR)){
				log.error("构建drools引擎失败>>"+kieBuilder.getResults().toString());
				throw new RuntimeException("构建drools引擎失败:\n" + kieBuilder.getResults().toString());
			}
		} catch (Exception e) {
			log.error("[load]加载规则失败",e);
		}
		log.info("[load]加载规则完毕");
	}
	
	public KieSession getkieSession(){
		KieContainer kieContainer = kieContainerMap.get("recoveryRule");
		KieSession kieSession = kieContainer.newKieSession();
		return kieSession;
		//return kieContainerMap.get("recoveryRule").newKieSession();
	}
	
	/**
	 * 2018年10月18日 下午2:05:06
	 * @return DroolsEngine
	 * 重新编译kieFileSystem中导入的规则
	 */
	public DroolsEngine prepareEngine() {
		if (build) {
			log.info("engine already prepared.");
			return this;
		}
		long start=System.currentTimeMillis();
		KieBuilder kieBuilder=kieServices.newKieBuilder(kieFileSystem);
		kieBuilder.buildAll();
		this.build=true;
		if(kieBuilder.getResults().hasMessages(Level.ERROR)){
			log.error("[prepareEngine]构建drools失败>>"+kieBuilder.getResults().toString());
			throw new RuntimeException("build error:\n" + kieBuilder.getResults().toString());
		}
		KieModule module = kieBuilder.getKieModule();
		kieRepository.addKieModule(module);
		if (kieContainer==null) {
			kieContainer=kieServices.newKieContainer(this.releaseId);
		}else {
			kieContainer.updateToVersion(releaseId);
		}
		kieBase=kieContainer.getKieBase(DroolsEngineConst.KIE_BASEMODEL_NAME);
		log.info("构建drools引擎完毕,耗时(毫秒)= "+(System.currentTimeMillis()-start));
		int ruleSize = kieBase.getKiePackages().toArray().length;
		log.info("已加载如下规则列表,数量={}",ruleSize);
		kieBase.getKiePackages().forEach(kiePackage ->{
			kiePackage.getRules().forEach(rule ->{
				log.info("rule:"+rule.getName()+",id:"+rule.getId()+",package:"+kiePackage.getName());
			});
		});
		return this;
	}
	
	/**
	 * 2018年10月18日 下午2:07:52
	 * @param content
	 * @param drlname
	 * @return
	 * @throws Exception DroolsEngine
	 * 指定规则名添加规则到KieFileSystem，规则路径src/main/resources/drlname
	 */
	public DroolsEngine addDRLs(String content,String drlname) throws Exception{
		if(content!=null && !"".equals(content.trim())){
			log.info("add rule>>"+drlname);
			String kieFilePath = DroolsEngineConst.DRL_FILE_WRITEPATH + drlname;
			kieFileSystem.delete(kieFilePath);
			kieFileSystem.write(kieFilePath, content);
			build = false;
		}
		return this;
	}
	
	/**
	 * 2018年10月18日 下午2:10:20
	 * @param drlname
	 * @return
	 * @throws Exception DroolsEngine
	 * 指定规则名删除KieFileSystem的规则,规则路径src/main/resources/drlname
	 */
	public DroolsEngine deleteDrls(String drlname)throws Exception{
		log.info("delete rule>>"+drlname);
		String kieFilePath = DroolsEngineConst.DRL_FILE_WRITEPATH + drlname;
		kieFileSystem.delete(kieFilePath);
		build = false;
		return this;
	}
	
	/**
	 * 2018年10月18日 下午2:11:04
	 * @return KieSession
	 */
	public KieSession createStatefulSession() {
		if(kieContainer == null){
			prepareEngine();
		}
		kieSession = kieContainer.newKieSession(DroolsEngineConst.KIE_STATEFULL_SESSION_NAME);
		return kieSession;
	}
	public StatelessKieSession createStateLessSession() {
		return kieContainer.newStatelessKieSession(DroolsEngineConst.KIE_STATELESS_SESSION_NAME);
	}
	/**
	 * @return the build
	 */
	public boolean isBuild() {
		return build;
	}
	/**
	 * @return the engineName
	 */
	public String getEngineName() {
		return engineName;
	}
	/**
	 * @param engineName the engineName to set
	 */
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}
	
	/**
	 * 2018年3月12日 上午10:57:49
	 * @param args void
	 */
	public static void main(String[] args) throws Exception{
		JSONArray rules=new JSONArray();
		DroolsEngine droolsEngineEPC=new DroolsEngine();
		droolsEngineEPC
			.initEngineBase("org.ks", "droolstest", "0.0.1-SNAPSHOT")
			.createKieFileSystem(new JSONObject())
			.prepareEngine();
		long start=System.currentTimeMillis();
		KieSession sessionEPC=droolsEngineEPC.createStatefulSession();
		log.info("insert resources data");
		sessionEPC.setGlobal("results", new ArrayList());
		HashMap paramMap = new HashMap();
		paramMap.put("key", 3); 
		paramMap.put("key1", 4); 
		paramMap.put("key2", 2);
		
		List list = new ArrayList();
		
		sessionEPC.insert(paramMap);
		//sessionEPC.insert(list);
		//sessionEPC.setGlobal("_$resources", resources);
		//sessionEPC.setGlobal("_$alerts", alerts);
		log.info("load resources data complete in "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
		log.info("test1 start: 数据清洗相关规则，事件级别变更，值映射，switchcase，添加、删除、更新");
		try {
			HashMap paramMap2 = new HashMap();
			paramMap2.put("key", 3);
			log.info("added new rule to engine.");
			sessionEPC.fireAllRules();
			sessionEPC.dispose();
		}catch (Exception e) {
			log.info("execute complete with error in "+(System.currentTimeMillis()-start),e);
		}
	}
}
