Blockly.Blocks['sensitive_select'] = {
  init: function() {
    this.appendValueInput("select")
        .setCheck("sensitive")
        .appendField(new Blockly.FieldTextInput("灵敏度配置"), "title");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};