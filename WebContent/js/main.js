 /**
   *增加模板行
   */
   
function addRow() {
    var table = document.getElementById("addTable");
    var tbody = document.getElementById("templeteTBody");
    var newTBody = tbody.cloneNode(true);
    newTBody.style.display="";
    var footTBody = document.getElementById("footTbody");
    return table.insertBefore(newTBody,footTBody);
   }
/**
   *删除模板行
   */
   
function deleteRow(obj) {
    var tbody = obj.parentNode.parentNode.parentNode;
    var table = document.getElementById("addTable"); 
    table.removeChild(tbody);
   }

/**
   *向模板中填充值
   */
function setValue(){

            var tbody=addRow();

    }