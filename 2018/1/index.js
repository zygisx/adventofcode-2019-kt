const input = require('./input');


const inter = { 0: 1 };
let sum = 0;
const numbers = input
for (i in numbers) {
  const num = numbers[i];
  sum += num;
  // console.log(num);
  // console.log(inter);
  if (inter[sum]) {
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
console.log(sum);