# Source

https://www.youtube.com/playlist?list=PLLMXbkbDbVt8tBiGc1y69BZdG8at1D7ZF

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