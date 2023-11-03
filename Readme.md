# Comment worker


## Welcome to comment worker

Comment worker responsibility

1. Subscribe the specific topic (post.comment.create) from RabbitMQ.
2. Create comment via POST http://topic-backend:4000/comments to topic service

## Starting project

Version 1 : We don't connect with Subject service

### Step 1 Make sure that your RabbitMQ and Topic service is running

If you don't know how to run these services, please go to this link https://github.com/Polapob/sw-arch-post-service

### Step 2 Update code in comment worker

Run this command before doing anything. Make sure that you are in main branch

```
git pull
```

### Step 3 Start comment worker

```
make run_worker
```

After the worker is running, a comment will be created in topic service when your publish createCommentEvent to RabbitMQ.
