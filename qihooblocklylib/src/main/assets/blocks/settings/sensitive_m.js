Blockly.Blocks['sensitive_medium'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("中"), "title");
    this.setOutput(true, "sensitive");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};