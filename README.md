# What do you do while the AI writes the code

Three small programs. Each one is a thing the video says out loud, and each one
is on screen as the output of running it rather than as a sentence somebody
typed.

Companion to the episode. Everything here is runnable end to end, with Docker
or without.

## The programs

| File | What it demonstrates | What it prints |
|---|---|---|
| `src/RetryNoKey.java` | "Add retries" implemented exactly as asked, with no idempotency key | `rows: 2`, and the customer charged twice |
| `src/RetryWithKey.java` | The same retry with a key derived from the ORDER, not the attempt | `rows: 1` |
| `src/StaleCache.java` | "Add a cache" implemented exactly as asked, with no invalidation story | database says `REFUNDED`, customer sees `PAID` |

None of these is a strawman. Each one is a competent implementation of a
completely reasonable instruction. The defect is in the request, and nobody
checked the request.

## Run it

With Docker, which needs nothing installed but Docker:

    ./docker-run.sh

Without Docker, with any JDK 21 or newer on the path:

    JAVA_HOME=/path/to/jdk ./run.sh RetryNoKey
    JAVA_HOME=/path/to/jdk ./run.sh RetryWithKey
    JAVA_HOME=/path/to/jdk ./run.sh StaleCache

Every program is a single file run directly by `java`, with no build tool, no
dependencies and no network access. They are deliberately small enough to read
in one sitting, because an example you have to trust is not an example.

## Why the timeout is on the way back

`RetryNoKey` is the one worth reading twice. Its fake provider RECORDS the
charge and then fails to respond. That is the shape of the real failure: the
request arrived, the work happened, and the answer was lost on the way home.
A retry with no idempotency key cannot tell that apart from a request that
never arrived, so it does the work again.

The fix is not more code. It is a key the provider can recognise.

## Regenerating the captures

    ./capture.sh

Writes `captures/*.txt`, one per program, each opening on the command that
actually produced it. The header is the real command on purpose: a header
claiming a flag nobody passed is a fabricated screenshot, which is worse than
no screenshot at all.
