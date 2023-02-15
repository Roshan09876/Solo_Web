console.log("This is js file");


const toggleSidebar = () => {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");
    if(sidebar.style.display === "block") {
        sidebar.style.display = "none";
        content.style.marginLeft = "0%";
    } else {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
};


tinymce.init({
    selector: '#mytextarea',
    plugins: [
        'a11ychecker','advlist','advcode','advtable','autolink','checklist','export',
        'lists','link','image','charmap','preview','anchor','searchreplace','visualblocks',
        'powerpaste','fullscreen','formatpainter','insertdatetime','media','table','help','wordcount'
    ],
    toolbar: 'undo redo | formatpainter casechange blocks | bold italic backcolor | ' +
        'alignleft aligncenter alignright alignjustify | ' +
        'bullist numlist checklist outdent indent | removeformat | a11ycheck code table help'
});


//JQuery Table
$(document).ready( function () {
    $('#myTable').DataTable();
} );



