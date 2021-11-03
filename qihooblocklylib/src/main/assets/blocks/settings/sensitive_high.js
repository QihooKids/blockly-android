Blockly.Blocks['sensitive_high'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("高"), "title");
    this.setOutput(true, "sensitive");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};