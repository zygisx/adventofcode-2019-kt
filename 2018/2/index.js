
const input = require('./input');


function count(id) {
  console.log(id);
  const dict = id.split('')
    .reduce((res, symbol) => {
      if (symbol in res) {
        res[symbol] = res[symbol] + 1;
      } else {
        res[symbol] = 1;
      }
      return res;
    }, {});

  const counts = Object.values(dict);
  const hasTwos = counts.some(count => count === 2);
  const hasThrees = counts.some(count => count === 3);

  console.log(hasTwos, hasThrees);

  return [hasTwos, hasThrees];
}

function main(ids) {
  const counts = ids.reduce((res, id) => {
    const [hasTwo, hasThree] = count(id);
    return { twos: res.twos + hasTwo, threes: res.threes + hasThree };
  }, { twos: 0, threes: 0})

  return counts.twos * counts.threes;
}

const answer = main(input);
console.log(answer);