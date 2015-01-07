function showHide(elm) {
    var rdoSLcheck = document.getElementById("rdoSL");

    var gr1 = document.getElementById("gr1");
    var gr1label = document.getElementById("gr1label");
    var cp1 = document.getElementById("cp1");
    var cp1label = document.getElementById("cp1label");
    var mp1 = document.getElementById("mp1");
    var mp1label = document.getElementById("mp1label");
    
    var gr2 = document.getElementById("gr2");
    var gr2label = document.getElementById("gr2label");
    var cp2 = document.getElementById("cp2");
    var cp2label = document.getElementById("cp2label");
    var mp2 = document.getElementById("mp2");
    var mp2label = document.getElementById("mp2label");
    
    var gr3 = document.getElementById("gr3");
    var gr3label = document.getElementById("gr3label");
    var cp3 = document.getElementById("cp3");
    var cp3label = document.getElementById("cp3label");
    var mp3 = document.getElementById("mp3");
    var mp3label = document.getElementById("mp3label");

    if(elm.id == 'rdoSL'){
        gr1.classList.remove('hide');
        gr1label.classList.remove('hide');
        cp1.classList.remove('hide');
        cp1label.classList.remove('hide');
        mp1.classList.remove('hide');
        mp1label.classList.remove('hide');

        gr2.classList.add('hide');
        gr2label.classList.add('hide');
        cp2.classList.add('hide');
        cp2label.classList.add('hide');
        mp2.classList.add('hide');
        mp2label.classList.add('hide');

        gr3.classList.add('hide');
        gr3label.classList.add('hide');
        cp3.classList.add('hide');
        cp3label.classList.add('hide');
        mp3.classList.add('hide');
        mp3label.classList.add('hide');
    }
    else if (elm.id == 'rdoBL'){
        gr1.classList.remove('hide');
        gr1label.classList.remove('hide');
        cp1.classList.remove('hide');
        cp1label.classList.remove('hide');
        mp1.classList.remove('hide');
        mp1label.classList.remove('hide');

        gr2.classList.remove('hide');
        gr2label.classList.remove('hide');
        cp2.classList.remove('hide');
        cp2label.classList.remove('hide');
        mp2.classList.remove('hide');
        mp2label.classList.remove('hide');

        gr3.classList.add('hide');
        gr3label.classList.add('hide');
        cp3.classList.add('hide');
        cp3label.classList.add('hide');
        mp3.classList.add('hide');
        mp3label.classList.add('hide');

    }
    else if (elm.id == 'rdoTL'){
        gr1.classList.remove('hide');
        gr1label.classList.remove('hide');
        cp1.classList.remove('hide');
        cp1label.classList.remove('hide');
        mp1.classList.remove('hide');
        mp1label.classList.remove('hide');

        gr2.classList.remove('hide');
        gr2label.classList.remove('hide');
        cp2.classList.remove('hide');
        cp2label.classList.remove('hide');
        mp2.classList.remove('hide');
        mp2label.classList.remove('hide');

        gr3.classList.remove('hide');
        gr3label.classList.remove('hide');
        cp3.classList.remove('hide');
        cp3label.classList.remove('hide');
        mp3.classList.remove('hide');
        mp3label.classList.remove('hide');

    }
}
