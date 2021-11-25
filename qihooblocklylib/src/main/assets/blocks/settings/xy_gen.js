Blockly.Lua['xy'] = function(block) {
  var number_x = block.getFieldValue('x');
  var number_y = block.getFieldValue('y');
  var value_xy = Blockly.Lua.valueToCode(block, 'xy', Blockly.Lua.ORDER_ATOMIC) || "";
  var code = '{'+number_x+","+number_y+"}";
  if(value_xy.length > 0){
      code = code+","+value_xy;
  }
  return [code, Blockly.Lua.ORDER_ATOMIC];
};