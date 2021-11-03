Blockly.Lua['time_duration_set'] = function(block) {
  var text_hour_s = block.getFieldValue('hour_s');
  var text_min_s = block.getFieldValue('min_s');
  var text_hour_e = block.getFieldValue('hour_e');
  var text_min_e = block.getFieldValue('min_e');
  var code = text_hour_s+'*60+'+text_min_s+", "+text_hour_e+'*60+'+text_min_e;
  return code;
};