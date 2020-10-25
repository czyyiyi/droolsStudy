
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class contrustTest {

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
        String drl_str= "package rules\r\n"
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

    /******生成releaseId******/
    public ReleaseId getReleaseId(){
        return new ReleaseIdImpl("org.default","artifact","1.0.0-SNAPSHOT");
    }


    /******通过kiesession执行规则匹配******/
    public void runKieSession(KieSession kieSession){
        try {
            kieSession.fireAllRules(
                    new AgendaFilter() {
                        @Override
                        public boolean accept(Match match) {
                            System.out.println("============MatchRuleName:" + match.getRule().getName() + "============");
                            return true;
                        }
                    }
            );
        }catch (Exception e){}
        finally {
            if(null != kieSession){
                kieSession.dispose();
            }
        }
    }









/*    @Test
    *//******通过kmodule配置文件******//*
    public void test1(){
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        //kieContainer内部也是通过kieBase创建kieSession
        KieSession kieSession = kieContainer.newKieSession("FileSystemKSession1");
        runKieSession(kieSession);

    }*/


    @Test
    /******通过KieModuleModel******/
    public void test2(){
        KieServices kieServices = KieServices.Factory.get();

        // 构建 kmodule.xml配置文件
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel(); //1为kmodule.xml创建KieModuleModel类
        KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel("FileSystemKBase")
                .addPackage("rules")//2给kmodule.xml中kbase属性（name和packages）赋值
                .setDefault(true)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        kieBaseModel.newKieSessionModel("FileSystemKSession")//3给kmodule.xml中kbase属性下kiesession属性（name）赋值
            .setDefault(true)
            .setClockType(ClockTypeOption.get("realtime"))
            .setType(KieSessionModel.KieSessionType.STATEFUL);



        // 加载 kmodule.xml配置文件 和 .drl规则文件
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        String kmoduleXml_str = kieModuleModel.toXML(); //4生成kmodule.xml文件内容


        kieFileSystem.writeKModuleXML(kmoduleXml_str);//5将这个xml文件写入到KieFileSystem中
        // 将drl规则文件加载到kieFileSystem
        kieFileSystem.write("src/main/resources/rules/rule11.drl",getDrlContent1());
        kieFileSystem.write("src/main/resources/rules/rule12.drl",getDrlContent2());//6 加载了两个drl配置文件

        // 根据 kmodule.xml配置文件 和 .drl规则文件 加载KieContainer模型
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll(); //7通过KieBuilder进行构建就将该kmodule加入到KieRepository中了。这样就将自定义的kmodule加入到引擎中

        // 获取构建的结果 如果存在异常的情况 作相应的处理
        Results results = kieBuilder.getResults();
        if(results.hasMessages(Message.Level.ERROR)){
            throw new RuntimeException("构建kieBuilder失败:\n"
                    + results.toString());
        }

        KieContainer kieContainer = kieServices.newKieContainer(getReleaseId());
        KieBase kieBase = kieContainer.getKieBase("FileSystemKBase");//使用kiemodule.xml中kbase属性的值
        //移除一个规则
        kieBase.removeRule("rules","rule-21");

        KieSession kieSession = kieBase.newKieSession();
        //KieSession kieSession = kieContainer.newKieSession("FileSystemKSession");//使用kiemodule.xml中kiesession属性的值
        runKieSession(kieSession);


    }



    @Test
    /******使用knowledgeBuilder创建******/
    public void test3() throws UnsupportedEncodingException {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        //装入规则，可以装入多个
        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(getDrlContent1().getBytes("UTF-8")), ResourceType.DRL);//以DRL形式加载规则
        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(getDrlContent2().getBytes("UTF-8")), ResourceType.DRL);
        KnowledgeBuilderErrors knowledgeBuilderErrors = knowledgeBuilder.getErrors();
        if(null != knowledgeBuilderErrors ){
            for (KnowledgeBuilderError error : knowledgeBuilderErrors) {
                System.out.println("==========KnowledgeBuilderErrors==========\n"+error);
            }
        }

        KnowledgeBaseImpl kBase = (KnowledgeBaseImpl)KnowledgeBaseFactory.newKnowledgeBase();

        //kBase.removeRule();
        Collection<KiePackage> packages = knowledgeBuilder.getKnowledgePackages();
        //System.out.println(packages);
        kBase.addPackages(packages);

        KieSession kieSession = kBase.newKieSession();

        runKieSession(kieSession);


    }





























}
