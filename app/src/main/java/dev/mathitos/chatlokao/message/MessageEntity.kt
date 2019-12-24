package dev.mathitos.chatlokao.message

class MessageEntity {
    var name: String
    var text: String
    var photoUrl: String?

    constructor() {
        this.name = ""
        this.text = ""
        this.photoUrl = null
    }

    constructor( name: String,  text: String,  photoUrl: String?){
        this.name = name
        this.text = text
        this.photoUrl = photoUrl
    }

}