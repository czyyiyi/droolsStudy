package utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class freemakerHelper extends CommonBase{



    private static final String ruletemplate_path = "freemaker-demo.drl";
    protected String ruleTemplate = "";
    private Configuration fmConfig; //freemarker模板配置对象
    private StringTemplateLoader stplLoader; //freemarker基于字符串的模板内容装载器

    public freemakerHelper(){
        fmConfig = new Configuration(Configuration.VERSION_2_3_23);
        stplLoader = new StringTemplateLoader();
        fmConfig.setTemplateLoader(stplLoader);
        loadRuleTemplate();
        stplLoader.putTemplate(SCRIPT_TEMPLATE_KEY,ruleTemplate);
    }




    /**
     * 加载模版
     */
    public void loadRuleTemplate(){
        StringBuilder template_builder = new StringBuilder();
        try{
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(ruletemplate_path);
            BufferedReader iss = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            try {
                String s;
                while((s = iss.readLine())!=null){
                    template_builder.append(s + "\n");
                }
            }finally {
                if(is!=null)
                    is.close();
                if(iss!=null)
                    iss.close();
            }
            ruleTemplate = template_builder.toString();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * 填充模版参数
     * @param scriptMap
     * @return
     */
    public String parseRuleScriptTemplate(Map scriptMap) {
        if(scriptMap == null ){
            return null;
        }
        Writer writer = new StringWriter();
        try {
            Template template = fmConfig.getTemplate(SCRIPT_TEMPLATE_KEY);
            template.process(scriptMap, writer);

        }catch(Exception e){

        }
        return writer.toString();
    }

    private Map<String,Object> getparseParamMap(){
        Map<String,Object> parseParamMap = new HashMap<String, Object>();
        parseParamMap.put(SCRIPT_RULE_NAME_KEY,"52656");
        parseParamMap.put(SCRIPT_RULE_PATTERNEXPRESSION_KEY,"sdfghjkl;edfgyhjukl");
        parseParamMap.put(SCRIPT_RULE_AGENDAGROUP_KEY,"3333");
        parseParamMap.put(SCRIPT_RULE_ALERTSTATUS_KEY,"6");
        return parseParamMap;
    }



    @Test
    private void test1() {

        freemakerHelper fmh = new freemakerHelper();

        String res = fmh.parseRuleScriptTemplate(getparseParamMap());

        System.out.println(res);
    }












}
