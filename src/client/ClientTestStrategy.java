/**
 * The client class was tested in end-to-end tests with the server class.
 * It has no JUnit tests for a few reasons:
 * 
 * -The individual methods do not lend themselves well to JUnit testing. Many of
 * them return void, or require a running server to test. While we could instantiate
 * a server, and inspect the canvas afterwards in a unit test, it is silly to do so
 * when it is much simpler and just as effective to user test the server.
 * 
 * -Most of the methods are very simple getters, setters, or helper functions, which
 * reduces the need for exhaustive unit testing.
 * 
 * -More importantly, a majority of the error-prone 'edge cases' for the client involve
 * server communication, concurrency, and relative timing, which JUnit is very ill-suited
 * to test.
 * 
 * Instead of JUnit testing, I employed the following user testing strategy:
 * 
 * -First, I opened up a server, and tested basic drawing capabilities all over the board. 
 * I checked different colors, sizes, etc. Then, I exited the server, reconnected, and ensured
 * the state was identical.
 * 
 * -Then, alongside my partners, I tested concurrent drawing to ensure that the client could hear
 * other clients strokes and make its own strokes at the same time. We also attempted to draw in the 
 * same area, to ensure that no concurrency bugs arose. At this point, we also tested that the user 
 * list display to ensure it was accurately updating by entering and exiting repeatedly with different
 * names.
 * 
 * -Next, we tested concurrent work on multiple different boards. Each member opened multiple clients
 * on different board numbers, and started using them, exiting and reentering frequently. We then compared
 * board states, to ensure that even under heavy use, the board got every stroke, and the clients dropped
 * no strokes.
 * 
 */

