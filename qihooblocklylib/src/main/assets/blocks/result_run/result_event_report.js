Blockly.Blocks['report_warning'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("警报消息推送"), "title")
        .appendField(new Blockly.FieldTextInput("推送提示消息"), "des")
        .appendField(new Blockly.FieldCheckbox("TRUE"), "check");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};