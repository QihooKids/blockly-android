Blockly.Lua['sensitive_medium'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.5';
  return [code, Blockly.Lua.ORDER_HIGH];
};