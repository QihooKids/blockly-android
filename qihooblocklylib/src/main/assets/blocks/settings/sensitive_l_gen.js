Blockly.Lua['sensitive_low'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.2';
  return [code, Blockly.Lua.ORDER_HIGH];
};