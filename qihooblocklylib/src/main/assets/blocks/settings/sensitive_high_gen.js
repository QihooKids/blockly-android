Blockly.Lua['sensitive_high'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.8';
  return [code, Blockly.Lua.ORDER_HIGH];
};