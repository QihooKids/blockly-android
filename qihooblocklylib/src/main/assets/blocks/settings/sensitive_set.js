Blockly.Blocks['sensitive_set'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("选择识别灵敏度")
        .appendField(new Blockly.FieldDropdown([["高","1"], ["中","2"], ["低","3"]]), "sensitive_select");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};