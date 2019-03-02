# Twitter mass follow
[![CircleCI](https://circleci.com/gh/indrekru/twitter-mass-follow.svg?style=svg)](https://circleci.com/gh/indrekru/twitter-mass-follow)

<img src="https://raw.githubusercontent.com/indrekru/mass-follower/master/img.png" width="200px">

This is a server-side twitter mass-follower + unfollower. It uses a scheduler and runs once every 24 hours, it runs as long as Twitter restrictions (rate limiting) for the day kick in, so it'll wait a day and do it again.
It will decide either to follow or unfollow a bunch of people, depending how many you are currently following. It unfollows people that have been followed for at least 2 days. It always needs to keep your current following number under 5000, otherwise - Twitter restrictions kick in.
If this thing runs on a scheduler every day, ideally your follower amount should grow.

It keeps track of followed/unfollowed users via HSQLDB local file storage database. Make sure the app can create a directory named `db` in the root directory and create/modify files inside it.
Also runs a cleanup job every day to clean up more than 3 months old entries, otherwise wouldn't be sustainable to store a DB in a xml file.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See running for notes on how to run the project on a system.

### Prerequisites

1. Clone the project to your local environment:
    ```
    git clone https://github.com/indrekru/twitter-mass-follow.git
    ```

2. You need to define environment variables:
   ```
   TWITTER_HOME_ACCOUNT_NAME
   TWITTER_BEARER_TOKEN
   TWITTER_CSRF_TOKEN
   TWITTER_COOKIE
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

## In production

This is project is currently in production.

### Health

https://mass-follower1.herokuapp.com/api/v1/health

### All followed records:

https://mass-follower1.herokuapp.com/api/v1/followed

### Follower stats:

https://mass-follower1.herokuapp.com/api/v1/follow-stats

### Commands

Deploy new version:
```
git push heroku master
```

Check logs:
```
heroku logs -tail -a mass-follower1
```

Restart:
```
heroku restart -a mass-follower1
```

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
