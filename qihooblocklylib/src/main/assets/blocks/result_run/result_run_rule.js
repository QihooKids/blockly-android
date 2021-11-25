Blockly.Blocks['result_run_rule'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("执行规则下一个")
        .appendField(new Blockly.FieldTextInput("1002"), "rule_id");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};