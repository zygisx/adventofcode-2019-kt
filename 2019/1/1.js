const input = require('./input');

function calculateFuel(mass) {
  return Math.floor(mass / 3) - 2
}

function calculateBulk(masses) {
  return masses.reduce(
    (sum, mass) => {
      console.log(mass, calculateFuel(mass));
      return sum + calculateFuel(mass)
    },
    0);
}

console.log(calculateBulk(input));