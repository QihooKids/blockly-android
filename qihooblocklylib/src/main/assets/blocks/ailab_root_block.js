Blockly.Blocks['ailab_root_block'] = {
  init: function() {
    this.appendStatementInput("ai_setttings")
        .setCheck(null)
        .appendField("添加AI识别配置");
    this.appendStatementInput("ai_functions")
        .setCheck(null)
        .appendField("添加需要执行的技能");
    this.appendStatementInput("ai_events")
        .setCheck(null)
        .appendField("如果技能执行成功那么执行");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};