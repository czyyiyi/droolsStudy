package utils;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.concurrent.ConcurrentHashMap;

/**
 * drools引擎类，最重要的是向外输出kieSession
 */
public class DroolsEngine {
    /**============variable============**/
    private static final String kieBaseMap_key = "kieBaseK";
    private final ConcurrentHashMap<String, KieBase> kieBaseMap = new ConcurrentHashMap<>();
//kie配置层变量
    private static final String kbaseName = "FileSystemKBase";
    private static final String ksessionName = "FileSystemKSession";
    /**drools官方api**/
    private KieServices kieServices;
    /**drools工作内存文件系统**/
    private KieFileSystem kieFileSystem;
    /**drools中kmodule.xml文件构建类***/
    private KieModuleModel kieModuleModel;
    /**丰富kmodule文件中kbase属性值,也是KieBase的雏形***/
    KieBaseModel kieBaseModel;
    /**丰富kmodule文件中ksession属性值,也是KieSession的雏形***/
    KieSessionModel kieSessionModel;
    /**drools发行版本号**/
    private ReleaseId releaseId;
//kie运行层变量
    /**kieSession的会话容器***/
    private KieContainer kieContainer;
    /**包含规则的drools知识系统***/
    private KieBase kieBase;


    /**============funtion============**/

    private static class  DroolsEngineHolder{

        private static final DroolsEngine droolsEngine = new DroolsEngine();
    }

    public static DroolsEngine getInstance(){
        return DroolsEngineHolder.droolsEngine;
    }


    public DroolsEngine(){
        long start=System.currentTimeMillis();
        kieServices = KieServices.Factory.get();
        releaseId = getReleaseId();
        add_kmodule2KieFileSystem(kbaseName,ksessionName);
        System.out.println("初始化dools引擎配置层耗时(毫秒)="+ (System.currentTimeMillis()-start));
    }

    /**
     * 生成releaseID
     */
    public ReleaseId getReleaseId(){
        return new ReleaseIdImpl("org.default","artifact","1.0.0-SNAPSHOT");
    }

    /**
     * 代码实现kmodule.xml配置文件功能
     */
    public DroolsEngine add_kmodule2KieFileSystem(String kbaseName,String KsessionName){
        kieFileSystem = kieServices.newKieFileSystem();
        // 构建 kmodule.xml配置文件
        kieModuleModel = kieServices.newKieModuleModel(); //1为kmodule.xml创建KieModuleModel类
        kieBaseModel = kieModuleModel.newKieBaseModel(kbaseName)//"FileSystemKBase"
                .addPackage("rules")//2给kmodule.xml中kbase属性（name和packages）赋值
                .setDefault(true)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        kieSessionModel = kieBaseModel.newKieSessionModel(KsessionName)//"FileSystemKSession"
                .setDefault(true)//3给kmodule.xml中kbase属性下kiesession属性（name）赋值
                .setClockType(ClockTypeOption.get("realtime"))
                .setType(KieSessionModel.KieSessionType.STATEFUL);
        // 加载 kmodule.xml配置文件到drools文件系统中
        String kmoduleXml_str = kieModuleModel.toXML(); //4生成kmodule.xml文件内容
        kieFileSystem.writeKModuleXML(kmoduleXml_str);//5将这个xml文件写入到KieFileSystem中

        return this;
    }

    /**
     * 解析并加载规则，创建运行时变量
     */
    public void loadRules(){
        // 将drl规则文件加载到kieFileSystem
        String rule1 = getDrlContent1();
        String rule2 = getDrlContent1();
        //path路径：src/main/resources/rules/ +drl文件名
        kieFileSystem.write("src/main/resources/rules/rule1.drl",rule1);
        kieFileSystem.write("src/main/resources/rules/rule2.drl",rule2);
        // 根据 kmodule.xml配置文件 和 .drl规则文件 加载KieContainer模型
        //7通过KieBuilder进行构建就将该kmodule加入到KieRepository中了。这样就将自定义的kmodule加入到引擎中
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        // 获取构建的结果 如果存在异常的情况 作相应的处理
        Results results = kieBuilder.getResults();
        if(results.hasMessages(Message.Level.ERROR)){
            throw new RuntimeException("构建kieBuilder失败:\n"
                    + results.toString());
        }
        kieContainer = kieServices.newKieContainer(releaseId);
        kieBase = kieContainer.getKieBase(kbaseName);//使用kiemodule.xml中kbase属性的值
        //移除一个规则
        //kieBase.removeRule("rules","rule-21");
        kieBaseMap.put(kieBaseMap_key,kieBase);
    }

    public KieSession getkieSession(){
        return kieBaseMap.get(kieBaseMap_key).newKieSession();
    }









    /******生成drl文件的内容******/
    public String getDrlContent1(){
        String drl_str= "package rulesP\r\n"
                + "rule \"rule-11\"\r\n"
                + "\twhen\r\n"
                + "\t\teval(true)\n"
                + "\tthen\r\n"
                + "\t\tSystem.out.println(\"Say Hello-11 By Code\");\n"
                + "end\r\n"
                ;
        return drl_str;
    }
    /******生成drl文件的内容******/
    public String getDrlContent2(){
        String drl_str= "package rulesP\r\n"
                + "rule \"rule-21\"\r\n"
                + "\twhen\r\n"
                + "\t\teval(true)\n"
                + "\tthen\r\n"
                + "\t\tSystem.out.println(\"Say Hello-21 By Code\");\n"
                + "end\r\n"
                + "rule \"rule-22\"\r\n"
                + "\twhen\r\n"
                + "\t\teval(true)\n"
                + "\tthen\r\n"
                + "\t\tSystem.out.println(\"Say Hello-22 By Code\");\n"
                + "end\r\n";
        return drl_str;
    }





}
