const input = require('./input');

function calculateFuel(mass) {
  return Math.floor(mass / 3) - 2
}

function calculateAllRequiredFuel(mass) {
  const fuelRequired = calculateFuel(mass);
  if (fuelRequired <= 0) {
    return 0;
  }

  return fuelRequired + calculateAllRequiredFuel(fuelRequired)
}

function calculateBulk(masses) {
  return masses.reduce(
    (sum, mass) => sum + calculateAllRequiredFuel(mass),
    0);
}

console.log(calculateBulk(input));