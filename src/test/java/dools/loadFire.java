package dools;

import details.AlertDetail;
import org.kie.api.runtime.KieSession;
import org.testng.annotations.Test;
import utils.CommonBase;
import utils.drools.DroolsEngine;
import utils.SnowflakeIdWorker;
import utils.freemakerHelper;

import java.util.*;

public class loadFire extends CommonBase {

    private static String[] agendaGroup_DB = {
           "app","os","storage","server","database","midlleware"
    };
    private static String[] resid_DB = {
            "101","102","103","104","105"
    };

    private SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
    /**
     * 一次随机地取num个元素
     * @param array
     * @param num
     * @param <T>
     * @return
     */
    public static <T> T[] randomSelected(T[] array, int num) {
        T[] temp = Arrays.copyOf(array, array.length);// 获得一个该数组的复制
        int length = temp.length;
        int left = length;
        while (length - left < num) {// length - left 为还需要计算多少次
            int i = (int) Math.floor(Math.random() * left--);// 随机选取一个元素，left 自减，这样不会覆盖上次产生的结果，并将下次选取的范围缩小
            T tmp = temp[i];// 将被选中的数与数组的最后一位进行调换
            temp[i] = temp[left];
            temp[left] = tmp;
        }
        return Arrays.copyOfRange(temp, 0, num > length ? length : num);// 从临时数组中复制出指定长度的数组
    }


    public List<Map<String,Object>> generateParamMaps(){
        List<Map<String,Object>> paramMapList = new ArrayList<>();

        for(String e: agendaGroup_DB){

            if(e.equals("app")){
                for(String o: resid_DB){
                    Map<String,Object> paramMap = new HashMap<String, Object>();
                    paramMap.put(SCRIPT_RULE_NAME_KEY,String.valueOf(idWorker.nextId()));
                    paramMap.put(SCRIPT_RULE_AGENDAGROUP_KEY,o);
                    paramMap.put(SCRIPT_RULE_PATTERNEXPRESSION_KEY,"");
                    paramMapList.add(paramMap);
                }
            }else{
                Map<String,Object> paramMap = new HashMap<String, Object>();
                paramMap.put(SCRIPT_RULE_NAME_KEY,String.valueOf(idWorker.nextId()));
                paramMap.put(SCRIPT_RULE_AGENDAGROUP_KEY,e);
                String pattern = "text contains \"haha\"";
                pattern = pattern.replace("haha",e);
                paramMap.put(SCRIPT_RULE_PATTERNEXPRESSION_KEY,pattern);
                paramMapList.add(paramMap);
            }

        }
        //System.out.println(paramMapList.size());
        return paramMapList;
    }

    @Test
    public void test1(){

        /***1.加载rule模版***/
        freemakerHelper fmh = new freemakerHelper();
        /***2.实例化rule模版形成ruleContent***/
        List<String> ruleContentList = new ArrayList<>();
        List<Map<String,Object>> paramMapList = generateParamMaps();
        for(Map<String,Object> p:paramMapList){
            String ruleContent = fmh.parseRuleScriptTemplate(p);
            //System.out.println(ruleContent);
            ruleContentList.add(ruleContent);
        }
        /***3.加载ruleContent***/
        DroolsEngine droolsEngine = new DroolsEngine();
        droolsEngine.loadRules(ruleContentList);
        /***4.加载fact***/
        AlertDetail ad = new AlertDetail(5678,"gff","os");
        /***4.fact和rule的匹配***/
        KieSession ks1 = droolsEngine.getkieSession();
        ks1.insert(ad);
        for(String e:agendaGroup_DB){
            if(e.equals("app")){
                for(String o: resid_DB){
                    //ks1.getAgenda().getAgendaGroup(o).setFocus();
                }
            }else{
                ks1.getAgenda().getAgendaGroup(e).setFocus();
            }
        }

        ks1.fireAllRules();

    }







}
