const input = require('./input');

function runProgram(program) {
  let iteration = 0;

  while (true) {
    const operation = program[iteration * 4]
    if (operation === 99) {
      return program;
    }
    const idx1 = program[iteration * 4 + 1]
    const idx2 = program[iteration * 4 + 2]
    const resIdx = program[iteration * 4 + 3]

    if (operation === 1) {
      program[resIdx] = program[idx1] + program[idx2];
    }
    if (operation === 2) {
      program[resIdx] = program[idx1] * program[idx2];
    }

    iteration++;
  }

  return program;
}

function first() {
  const inputCopy = input.slice(0);
  inputCopy[1] = 12;
  inputCopy[2] = 2;

  const result = runProgram(inputCopy);
  console.log(result[0]);
}


function second() {
  for (let noun = 0; noun < 100; noun++) {
    for (let verb = 0; verb < 100; verb++) {
      const inputCopy = input.slice(0);
      inputCopy[1] = noun;
      inputCopy[2] = verb;
      const result = runProgram(inputCopy);
      if (result[0] === 19690720) {
        console.log(100 * noun + verb);
      }
    }
  }  
}

first();
second();