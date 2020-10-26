package utils;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class DroolsEngine_kmodule extends CommonBase{
    /**drools官方api**/
    private KieServices kieServices;

    /**kieSession的会话容器***/
    private KieContainer kieContainer;



    private static class  DroolsEngineHolder {
        private static final DroolsEngine_kmodule droolsEngine_kmodule = new DroolsEngine_kmodule();
    }

    public static DroolsEngine_kmodule getInstance(){
        return DroolsEngineHolder.droolsEngine_kmodule;
    }

    public DroolsEngine_kmodule(){
        kieServices = KieServices.Factory.get();
    }

    public void loadRules(){
        kieContainer = kieServices.getKieClasspathContainer();
        kieContainerMap.put(kieContainerMap_key,kieContainer);
    }

    public KieSession getkieSession(){
        return kieContainerMap.get(kieContainerMap_key).newKieSession(ksessionName+"1");//FileSystemKSession1
    }




}
