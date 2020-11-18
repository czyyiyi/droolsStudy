
import details.AlertDetail;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.testng.annotations.Test;
import utils.DroolsEngine;
import utils.DroolsEngineLight;


import java.io.UnsupportedEncodingException;



public class app {


    @Test
    public void DroolsEngineTest(){
        DroolsEngine droolsEngine = new DroolsEngine();
        long start1=System.currentTimeMillis();
        droolsEngine.loadRules();
        KieSession ks1 = droolsEngine.getkieSession();
        AlertDetail ad = new AlertDetail(5678,"hh数据hh","yyPitt");
        ks1.insert(ad);
        droolsEngine.runKieSession(ks1);
        //System.out.println(">>>>>>drools匹配耗时(毫秒)="+ (System.currentTimeMillis()-start1));
    }

    @Test
    public void DroolsEngineLightTest() throws UnsupportedEncodingException {
        DroolsEngineLight droolsEngineLight = new DroolsEngineLight();
        long start2=System.currentTimeMillis();
        droolsEngineLight.loadRules();
        KieSession ks2 = droolsEngineLight.getkieSession();
        droolsEngineLight.runKieSession(ks2);
        System.out.println(">>>>>>droolsLight初始化并匹配耗时(毫秒)="+ (System.currentTimeMillis()-start2));
    }




}
