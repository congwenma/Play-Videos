# Source

https://www.youtube.com/playlist?list=PLLMXbkbDbVt8tBiGc1y69BZdG8at1D7ZF

Skipping 22-24, e2e framework not working
Skipping Jquery, React (not using any scala asset pipeline in this tutorial).

# Start the app
Seems to require manually running the files in `./sql` directory. Make sure to run the table create queries as the `playvideos` user

> psql tasklist -U playvideos



For the purpose of using time efficiently, skip v1-v3, and go to `http://localhost:9000/v5/index`

# Notes

- on `sbt >`, `run` does the trick of `web/run`

- Routes

  - In routes, `/*file` will match everything after, but needs to be last

  - In routes, `/$prodType<[a-z]{2}\d{3}>/...`, you can use regexp to match routes

  - Play will return `unauthorized` if you try to use `POST`, so adding this `+ nocsrf` before the route

    ```
    +nocsrf
    POST    /validatePost     controllers.TaskList1.validatePost
    ```





- To escape `Option[]`, use `.map`



- To use CSRF, we need to `implicit request` in the view, which removes need to pass `request` to `view`.

