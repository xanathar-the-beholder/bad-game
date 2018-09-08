[![BCH compliance](https://bettercodehub.com/edge/badge/xanathar-the-beholder/bad-game?branch=master)](https://bettercodehub.com/)

# Bad Game

Proof that bad code can get a 10/10 rating on `bettercodehub.com`

![BCH compliance](./bettercodehub-20180908.png)

## Background

I wrote this game for the [Java4K competition](http://www.java4k.com/index.php?action=games&method=view&gid=216) back in 2009. 
The objective of the competition is to put as much game as possible in a 4096 byte jar file.
This results in awful code because use of methods and fields eat up bytes quickly.
So the whole game was written mostly in the constructor, and almost all variables are local.

A perfect candidate make it score 10/10 :-)

## Why?

Mostly because I wanted to know how bettercodehub works.

Also as a warning to any managers, auditors and consultants that think that if a tool says the code is good, the code is good.

> There are things that can be measured. There are things that are worth measuring. But what can be measured is not always what is worth measuring; what gets measured may have no relationship with what we really want to know.

_(from 'The Tyranny of Metrics' by Jerry Z. Muller)_

See also [Goodhart's Law](https://en.wikipedia.org/wiki/Goodhart%27s_law) about the gaming of metrics.

## Refactoring to make things worse.

Most of the following tips should NEVER be performed on actual production code. 

### 4 Keep unit interfaces small

This is about the number of parameters the methods consume.
- Turn all local variables into fields. No parameters needed. If you need concurrency make everything synchronized.

### 1 Write Short Units of Code

This is the line count of the method. 
- use the `extract to method` feature of your IDE to break up long methods. [Good names](https://www.youtube.com/watch?v=SctS56YQ6fg) are not scored.
- simply put multiple statements on one line (don't accidently apply the code formatter later!)
- remove comments.

### 2 Write Simple Units of Code

This is the number of branch points in the method.
- use the `extract to method` feature of your IDE to break up long methods.
- use the `extract to method` feature of your IDE put conditionals in a separate method.

Is a class holding lots of short and simple methods with few arguments easier to maintain? 
Not necessarily. Read [Small functions considered harmful](https://medium.com/@copyconstruct/small-functions-considered-harmful-91035d316c29)

### 7 Keep Architecture components balanced.

This means that at a specific folder level there should be at least 2 folders which contain roughly the same amount of code.
I solved this by making a copy of the game in another package.

Then I had to deal with some duplication.

### 3 Write Code Once  

This checks for duplicate lines. 
- rename all fields to something else.
- rename method arguments to something else.
- if possible change the order of statements.
- if the above fails, rename the method.

Also test code is not evaluated for duplication. 

### 9 Automate tests

Your test code should be at least half the number of lines of your production code.
- It is not necessary to write tests that test something. 
- It is not necessary to have tests that pass (!).
- You do need lots of asserts.
- You do need to reference your class under test in some way.

The following will count as a valid test (and 7 lines with 57% asserts), but doesn't test anything:

```
    @Test
    public void shouldTest() {
        assertTrue(true);
        assertTrue(true);
        assertTrue(true);
        assertTrue(true);
    }
```

I simply made a few long test methods with lots of asserts in them.
 
### 10 Write Clean Code

There are some code smell detections built in. I've only encountered:
- Code in comments: simply remove the comments.

Bettercodehub doesn't seem to detect magic values. The code is full of them :-)

## Bonus: how to crash the analyzer

Add a method with lots of semicolons;

```
    public void crash() {
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    }
```

![BCH crashes on semicolons](./bch-semicolon-crash.png)

# Conclusion

A fool with a tool is still a fool :-)

