
var WizardCommonElements = function () {

    this.getPage = function () {
        browser.get(browser.params.url);
    };

    this.clickById = function (name) {
        element(by.id(name)).click();
    };

    this.clickByModel = function (name) {
        element(by.model(name)).click();
    };

    this.testTextInput = function (model) {
        console.log("text-input-model: " + model);
     //   console.log("text-input-buttonId: " + buttonId);
        var elementId = model.split(".")[1];
        console.log("text-input-elementId: " + elementId);
      //  this.clickById(buttonId);
        var userInput = element(by.id(elementId));
        userInput.clear().sendKeys('testing');
         expect(userInput.getAttribute('value')).toEqual('testing');
    };

    this.testTextArea = function(model) {
        console.log("text-area-model: " + model);
    //    console.log("text-area-buttonId: " + buttonId);
        var elementId = model.split(".")[1];
        console.log("text-area-elementId: " + elementId);
        var userInput = element(by.id(elementId));
/*        userInput.sendKeys('testing');
        expect(userInput.getAttribute('value')).toEqual('testing');*/
    };

    this.testDropdownSelectInput = function (model) {
        console.log("drop-down-select: " +model);
       // this.clickById(buttonId);
        this.clickByModel(model);
        var elementId = model.split(".")[1];
        console.log("elementId: " + elementId);
        element(by.model(model)).all(by.id(elementId)).each(function (element, index) {
            element.getText().then(function (text) {
                var items = text.split('\n');
                console.log(items.length);
                console.log(items[1] + " : " + items[items.length-2]);
                expect(items.length).toBeGreaterThan(0);
                expect(items[items.length - 1]).toBe('Other');
            });
        });
    };

    this.testDropdownSelectEdit = function (model) {
        console.log("drop-down-edit: " +model);
       // this.clickById(buttonId);
      //  this.clickByModel(model);
        var elementId = model.split(".")[1];
        console.log("elementId: " + elementId);
        var elem = browser.findElement(By.id(elementId));
        elem.click();
            elem.getText().then(function (text) {
                var items = text.split('\n');
                console.log(items);
                console.log(items.length);
                console.log(items[1] + " : " + items[items.length-2]);
                expect(items.length).toBeGreaterThan(0);
                expect(items[items.length - 1]).toBe('Other');
            });
    };

    this.testMultiSelectInput = function (model) {
        console.log("multi-select model: " +model);
       // console.log("multi-select buttonId: " + buttonId );
        var elementId = model.split(".")[1];
        console.log("elementId: " + elementId);
      //  this.clickById(buttonId);
        element(by.id(elementId)).click();
        //element(by.model(model)).click().then(function(){
        var elem = browser.findElement(by.model(model));
          //  element(by.model(model)).all(by.css('.suggestion-list')).each(function (element, index) {
                elem.getText().then(function (text) {
                    var items = text.split(' ');
                    console.log(items.length);
                    expect(items.length).toBeGreaterThan(0);
                });
          //  });
        //});
    };

    this.testCheckBoxInput = function (model) {
        console.log("checkbox: " + model);
        var elementId = model.split(".")[1];
        console.log("elementId: " + elementId);
     //   this.clickById(buttonId);
        var chekBox = element(by.id(elementId));
        if(!chekBox.isSelected()){
            console.log("not checked");
            chekBox.click();
        }
        expect(chekBox.isSelected()).toBe(false);
    };

    this.testBinding = function(model){
        console.log("test binding "  + model);
       expect(element(by.exactBinding(model)).isPresent()).toBe(true);
    };


    this.testModelBinding = function (model) {
            console.log(model);
        var ret = element(by.exactBinding(model));
            return ret.getText();
    };

    this.testInputFields = function(elements, breadcrumb){
        var fields = elements.fields;
        for (var i = 0; i < fields.length; i++) {
            var elementType = fields[i].type;
            var model = fields[i].model;
                this.getPage();
            if(breadcrumb) {
                for (var j = 0; j < breadcrumb.length; j++) {
                    console.log(breadcrumb[0]);
                    var button = browser.findElement(By.id(breadcrumb[j]));
                    button.click();
                }
            }
            switch (elementType) {
                case "text-input":
                    this.testTextInput(model);
                    break;
                case "text-box":
                    this.testTextArea(model);
                    break;
                case "dropdown-select":
                    this.testDropdownSelectInput(model);
                    break;
                case "dropdown-edit":
                    this.testDropdownSelectEdit(model);
                    break;
                case "multi-select":
                    this.testMultiSelectInput(model);
                    break;
                case "check-box":
                    this.testCheckBoxInput( model);
                    break;
                case "binding":
                    console.log(model);
                    this.testBinding(model);
                    break;
                case "form-selector":
                    var parentFormBtn = elements.buttonId +"-toggle";
                    var newFormToggleBtn = elements.formObj +"-" + model;
                    if(breadcrumb.length == 0 ) {
                        breadcrumb = [parentFormBtn, newFormToggleBtn];
                    }else{
                    breadcrumb.push(newFormToggleBtn);
                    }
                    switch(model){
                        case 'access':
                            var accessElementTests = require('./AccessFormTest.js');
                            var accessPage = new accessElementTests;
                            this.testInputFields(accessPage.formElements, breadcrumb);
                            break;
                        case 'reference':
                            var refElementTests = require('./ReferenceFormTest.js');
                            var refPage = new refElementTests;
                            this.testInputFields(refPage.formElements, breadcrumb);
                            break;
                        case 'comments':
                            var commentElementTests = require('./CommentFormTest.js');
                            var commentPage = new commentElementTests;
                            this.testInputFields(commentPage.formElements, breadcrumb);
                            break;
                        /*case 'reference':
                            break;
                        case 'reference':
                            break;
*/
                    }
                    break;
            } //switch
        } //for i
    }


};
module.exports = WizardCommonElements;