Blockly.Blocks['time_available1'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("全天警戒"), "title")
        .appendField(new Blockly.FieldTextInput("24小时"), "des");
    this.setOutput(true, "time_available");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};