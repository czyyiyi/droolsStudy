package utils;


import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class DroolsEngineLight extends CommonBase{

    private KnowledgeBuilder knowledgeBuilder;
    private KnowledgeBaseImpl kBase;

    private static class  DroolsEngineHolder{
        private static final DroolsEngineLight droolsEngineLight = new DroolsEngineLight();
    }

    public static DroolsEngineLight getInstance(){
        return DroolsEngineLight.DroolsEngineHolder.droolsEngineLight;
    }


    public DroolsEngineLight(){
        long start=System.currentTimeMillis();
        knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        System.out.println("初始化doolsLight引擎配置层耗时(毫秒)="+ (System.currentTimeMillis()-start));
    }


    public void loadRules() throws UnsupportedEncodingException {
        String rule1 = getDrlContent1();
        String rule2 = getDrlContent2();
        //装入规则，可以装入多个
        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(rule1.getBytes("UTF-8")), ResourceType.DRL);//以DRL形式加载规则
        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(rule2.getBytes("UTF-8")), ResourceType.DRL);

        KnowledgeBuilderErrors knowledgeBuilderErrors = knowledgeBuilder.getErrors();
        if(null != knowledgeBuilderErrors ){
            for (KnowledgeBuilderError error : knowledgeBuilderErrors) {
                System.out.println("==========KnowledgeBuilderErrors==========\n"+error);
            }
        }

        kBase = (KnowledgeBaseImpl) KnowledgeBaseFactory.newKnowledgeBase();
        //kBase.removeRule();
        Collection<KiePackage> packages = knowledgeBuilder.getKnowledgePackages();
        kBase.addPackages(packages);

        kBaseMap.put(kBaseMap_key,kBase);

    }

    public KieSession getkieSession() {
        return kBaseMap.get(kBaseMap_key).newKieSession();
    }






}
