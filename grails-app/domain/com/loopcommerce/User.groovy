package com.loopcommerce

class User {

    String userId
    String password
    String homepage
    Date dateCreated
    Profile profile

    static hasMany = [posts: Post, tags: Tag, following: User]


    static constraints = {
        userId(blank: false, size: 3..20, unique: true)
        password(
                blank: false,
                size: 6..8,
                validator: { passwd, user -> return passwd != user.userId}
        )
        dateCreated()
        homepage(url: true, nullable: true)
        profile nullable: true
    }
    static mapping = {
        profile lazy: false
        posts sort: 'dateCreated'
    }

}
