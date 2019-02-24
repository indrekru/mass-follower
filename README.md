# Mass follower
[![CircleCI](https://circleci.com/gh/indrekru/mass-follower.svg?style=svg&circle-token=511cd6b194fb4557acab2a962d87df04ce5dcb37)](https://circleci.com/gh/indrekru/mass-follower)

![](img.png?s=50)

This is a server-side twitter mass-follower + unfollower.
It will follow bunch of people and then unfollow the ones you already have been following for at least 2 days.
If this thing runs every day, ideally your follower amount should grow.

It keeps track of followed/unfollowed users via HSQLDB local file storage database. Make sure the app can create a directory named `db` and create/modify files inside it.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See running for notes on how to run the project on a system.

### Prerequisites

1. Clone the project to your local environment:
    ```
    git clone https://github.com/indrekru/mass-follower.git
    ```

2. You need to define environment variables in your
   `.bash_profile` like this:
   ```
   export TWITTER_HOME_ACCOUNT_NAME='[HOME_ACCOUNT_NAME]'
   export TWITTER_BEARER_TOKEN='[BEARER_TOKEN]'
   export TWITTER_CSRF_TOKEN='[CSRF_TOKEN]'
   export TWITTER_COOKIE='[COOKIE]'
   ```
   
   #### Where to find these values
   
   Log in to your twitter account and open up developer tools.
   
   * `TWITTER_HOME_ACCOUNT_NAME` - your own account name in twitter
   
   * `TWITTER_BEARER_TOKEN` - Passed in requests as header called:
       ```
       authorization
       ```
        Looks like:
       ```
       'Bearer whateverRandomNumbersLetters...'
       ```
   * `TWITTER_CSRF_TOKEN` - passed as header named:
       ```
       x-csrf-token
       ```
   * `TWITTER_COOKIE` - See your twitter cookie and extract 2 values:
       ```
       auth_token=whatever; ct0=whatever2;
       ```

3. You need maven installed on your environment:

    #### Mac (homebrew):
    
    ```
    brew install maven
    ```
    #### Ubuntu:
    ```
    sudo apt-get install maven
    ```

### Installing

Once you have maven installed on your environment, install the project dependencies via:

```
mvn install
```

## Testing

Run all tests:
```
mvn test
```

## Running

Once you have installed dependencies, this can be run from the `Application.java` main method directly,
or from a command line:
```
mvn spring-boot:run
```

And now if all went well, watch the terminal spit out logs as it's doing its magic.

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot 2
* [Spock](http://spockframework.org/) - Spock testing framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [HSQLDB](http://hsqldb.org/) - Local file storage database

## Contributing

If you have any improvement suggestions please create a pull request and I'll review it.


## Authors

* **Indrek Ruubel** - *Initial work* - [Github](https://github.com/indrekru)

See also the list of [contributors](https://github.com/indrekru/design-patterns-spring-boot/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License

## Acknowledgments

* Big thanks to Pivotal for Spring Boot framework, love it!
