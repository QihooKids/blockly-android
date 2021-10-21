package com.qihoo.ailab.model;

/**
 * The files for the AI rule
 */
public class AIRuleFileData {

    private String pathToolbox;

    private String pathJson;

    private String pathCodeJs;

    private String pathRule;

    public AIRuleFileData(String pathToolbox, String pathJson, String pathCodeJs, String pathRule) {
        this.pathToolbox = pathToolbox;
        this.pathJson = pathJson;
        this.pathCodeJs = pathCodeJs;
        this.pathRule = pathRule;
    }

    public String getPathToolbox() {
        return pathToolbox;
    }

    public void setPathToolbox(String pathToolbox) {
        this.pathToolbox = pathToolbox;
    }

    public String getPathJson() {
        return pathJson;
    }

    public void setPathJson(String pathJson) {
        this.pathJson = pathJson;
    }

    public String getPathCodeJs() {
        return pathCodeJs;
    }

    public void setPathCodeJs(String pathCodeJs) {
        this.pathCodeJs = pathCodeJs;
    }

    public String getPathRule() {
        return pathRule;
    }

    public void setPathRule(String pathRule) {
        this.pathRule = pathRule;
    }
}
