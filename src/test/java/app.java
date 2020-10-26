
import org.kie.api.runtime.KieSession;
import utils.DroolsEngine;
import utils.DroolsEngineLight;
import utils.DroolsEngine_kmodule;

import java.io.UnsupportedEncodingException;


public class app {

    public static void main(String[] args) throws UnsupportedEncodingException {
        DroolsEngine_kmodule droolsEngine_kmodule = new DroolsEngine_kmodule();
        DroolsEngine droolsEngine = new DroolsEngine();
        DroolsEngineLight droolsEngineLight = new DroolsEngineLight();

        long start1=System.currentTimeMillis();
        droolsEngine.loadRules();
        KieSession ks1 = droolsEngine.getkieSession();
        droolsEngine.runKieSession(ks1);
        System.out.println("drools匹配耗时(毫秒)="+ (System.currentTimeMillis()-start1));

        long start2=System.currentTimeMillis();
        droolsEngineLight.loadRules();
        KieSession ks2 = droolsEngineLight.getkieSession();
        droolsEngineLight.runKieSession(ks2);
        System.out.println("droolsLight匹配耗时(毫秒)="+ (System.currentTimeMillis()-start2));

/*        long start3=System.currentTimeMillis();
        droolsEngine_kmodule.loadRules();
        KieSession ks3 = droolsEngine_kmodule.getkieSession();
        droolsEngine_kmodule.runKieSession(ks3);
        System.out.println("drools_kmodule匹配耗时(毫秒)="+ (System.currentTimeMillis()-start3));*/

    }
}
