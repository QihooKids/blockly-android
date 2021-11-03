Blockly.Lua['report_warning'] = function(block) {
  var text_title = block.getFieldValue('title');
  var text_des = block.getFieldValue('des');
  var checkbox_check = block.getFieldValue('check') == 'TRUE';
  var code = '\n';
  if(!checkbox_check){
     return 'ret = \"\" \n';
  }
  return code;
};