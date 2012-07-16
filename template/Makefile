JAVAC = javac
JFLAGS = 

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

twoplayer: TwoPlayerController.class
	java TwoPlayerController

oneplayer: OnePlayerController.class
	java OnePlayerController

.PHONY: clean all

all: twoplayer

clean:
	rm *.class