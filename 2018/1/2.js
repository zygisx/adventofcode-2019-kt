const input = require('./input');

const inter = { 0: 1};
let sum = 0;

function doFreq() {
  const numbers = input
  for (i in numbers) {
    const num = numbers[i];
    sum += num;
    console.log(num);
    // console.log(inter);
    if (sum in inter) {
      inter[sum] = inter[sum] + 1;
    } else {
      inter[sum] = 1
    }
    // inter[sum] = inter[sum] ? inter[sum] + 1 : 1;
    if (inter[sum] > 1) {
      console.log(sum);
      break;
    }
  }
  return inter, sum;
}


while(inter[sum] <= 1) {
  doFreq();
  console.log(inter[sum]);
}

console.log(sum);