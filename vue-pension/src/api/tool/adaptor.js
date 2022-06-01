//添加时间范围选择添加滑动事件
function addTouchmove(selector){
  let picker = document.querySelector(selector);
  if(picker == null) return;
  picker.addEventListener("touchstart",function(es){
    let startX = es.touches[0].clientX - picker.offsetLeft;
    picker.addEventListener("touchmove",function(e) {
      picker.style.left = e.touches[0].clientX - startX  + 'px';
    })
  })
}

export {addTouchmove}
