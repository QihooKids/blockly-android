package com.qihoo.ailab.model;

/**
 * The files for the AI rule
 */
public class AIRuleFileUrl {

    private AIRule rule;

    private String pathToolbox;

    private String pathBlockJson;

    private String pathCodeJs;

    private String pathRuleXml;

    public AIRuleFileUrl(AIRule rule, String pathToolbox, String pathJson, String pathCodeJs, String pathRule) {
        this.pathToolbox = pathToolbox;
        this.pathBlockJson = pathJson;
        this.pathCodeJs = pathCodeJs;
        this.pathRuleXml = pathRule;
        this.rule = rule;
    }

    public String getPathToolbox() {
        return pathToolbox;
    }

    public void setPathToolbox(String pathToolbox) {
        this.pathToolbox = pathToolbox;
    }

    public String getPathBlockJson() {
        return pathBlockJson;
    }

    public void setPathBlockJson(String pathBlockJson) {
        this.pathBlockJson = pathBlockJson;
    }

    public String getPathCodeJs() {
        return pathCodeJs;
    }

    public void setPathCodeJs(String pathCodeJs) {
        this.pathCodeJs = pathCodeJs;
    }

    public String getPathRule() {
        return pathRuleXml;
    }

    public void setPathRule(String pathRule) {
        this.pathRuleXml = pathRule;
    }

    public AIRule getRule() {
        return rule;
    }

    public void setRule(AIRule rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "AIRuleFileData{" +
                "rule=" + rule +
                ", pathToolbox='" + pathToolbox + '\'' +
                ", pathBlockJson='" + pathBlockJson + '\'' +
                ", pathCodeJs='" + pathCodeJs + '\'' +
                ", pathRuleXml='" + pathRuleXml + '\'' +
                '}';
    }
}
