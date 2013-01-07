package com.loopcommerce

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(User)
class UserIntegrationSpec extends spock.lang.Specification {

    def "Saving user"() {
        given: "New user instance"
        def joe = new User(userId: 'joe', password: 'secret', homepage: 'http://www.yahoo.com')

        when: "the user is saved"
        joe.save()

        then: "it saved successfully and can be found in the database"
        joe.errors.errorCount  == 0
        joe.id != null
        User.get(joe.id).userId  == joe.userId
        log.info("user get saved and retrieved")
        log.info(joe)
    }

    def "Updating existing user"() {
        given: "An existing user"
        def existingUser  = new User(userId: 'joe', password: 'secret', homepage: 'http://www.yahoo.com')
                .save(failOnError: true)
        when: "A property is changed"
        def foundUser = User.get(existingUser.id)
        foundUser.password = 'sesame'
        foundUser.save(failOnError: true)

        then: "The change is reflected in the database"
        User.get(existingUser.id).password == 'sesame'


    }

    def "Deleting an existing user removes it from database"(){
        given: "An existing user"
        def user = new User(userId: 'joe', password: 'secret', homepage: 'http://www.yahoo.com')
                .save(failOnError: true)
        when: "The user is deleted"
        def foundUser = User.get(user.id)
        foundUser.delete(flush: true)

        then: "The user is removed from the database"
        !User.exists(foundUser.id)
    }

    def "Saving a user with invalid properties causes an error"(){

        given: "A user which fails several field validations"
        def user = new User(userId: 'chuck_norris', password: 'tiny', homepage: 'not-a-url')

        when: "The user is validated"
        user.validate()

        then:
        user.hasErrors()
        "size.toosmall" == user.errors.getFieldError("password").code
        "tiny" == user.errors.getFieldError("password").rejectedValue
        "url.invalid" == user.errors.getFieldError("homepage").code
        "not-a-url" == user.errors.getFieldError("homepage").rejectedValue
        !user.errors.getFieldError("userId")

    }

    def "Recovering from a failed save by fixing invalid properties"(){
        given: "A user that has invalid properties"
        def chuck = new User(userId: 'chuck_norris', password: 'tiny', homepage: 'not-a-url')
        assert chuck.save() == null
        assert chuck.hasErrors()

        when: "We fix the invalid properties"
        chuck.password = "fistfist"
        chuck.homepage = "http://www.yahoo.com/"
        chuck.validate()

        then: "The user saves and validates fine"
        !chuck.hasErrors()
        chuck.save()
    }
}
