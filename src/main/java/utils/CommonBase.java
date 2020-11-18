package utils;

import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import java.util.concurrent.ConcurrentHashMap;

public class CommonBase {

    protected static final String kieContainerMap_key = "kieContainer_key";
    protected final ConcurrentHashMap<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();

    protected static final String kieBaseMap_key = "kieBase_key";
    protected final ConcurrentHashMap<String, KieBase> kieBaseMap = new ConcurrentHashMap<>();

    protected static final String kBaseMap_key = "kBase_key";
    protected final ConcurrentHashMap<String, KnowledgeBaseImpl> kBaseMap = new ConcurrentHashMap<>();

    protected static final String kbaseName = "FileSystemKBase";
    protected static final String ksessionName = "FileSystemKSession";
    protected static final String packagesName = "rules";
    /******生成drl1文件的内容******/
    public String getDrlContent1(){
        String drl_str= "package rules\r\n"
                + "rule \"rule-11\"\r\n"
                + "\twhen\r\n"
                + "\t\teval(true)\n"
                + "\tthen\r\n"
                + "\t\tSystem.out.println(\"Say Hello-11 By Code\");\n"
                + "end\r\n"
                ;
        return drl_str;
    }
    /******生成drl2文件的内容******/
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
    /******生成drl3文件的内容******/
    public static String getDrlContent3(){
        String res = "package rules \r\n"
                + "import details.AlertDetail; \r\n"
                + "rule \"alertGroup\"\r\n"
                + "\t when \r\n"
                + "\t\t $alertDetail : AlertDetail(title contains \"Pi\" && text contains \"数据\"); \r\n"
                + "\t then \r\n"
                + "\t\t System.out.println(\"gdyh\");\r\n"
                + "end\r\n";

        return res;
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

}
