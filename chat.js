function addMessage() {
    $.post(
        "/add-message",
        {
            author: $("#author").val(),
            text: $("#text").val()
        },
        function(data) {
            $("#text").val(""); //clears text field

        }
    );
}

function getMessages() {
    $.get(
        "/get-messages",
        function(data) {
            var messages = JSON.parse(data);
            $("#messages").empty(); //clears messages
            for (var i in messages) {
                var elem = $("<div>").text(messages [i].author + ": " + messages[i].text);
                $("#messages").append(elem);

            }
        }
    );
}
//getMessages();

setInterval(getMessages, 1000); //to repeatedly check messages