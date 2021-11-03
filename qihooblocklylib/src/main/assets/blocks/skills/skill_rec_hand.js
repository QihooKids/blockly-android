Blockly.Blocks['human_detect'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("人形检测"), "title")
        .appendField(new Blockly.FieldTextInput("针对有人出现的情况进行报警"), "des")
        .appendField(new Blockly.FieldCheckbox("TRUE"), "check");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};