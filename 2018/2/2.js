
const input = require('./input');


function hasOneDiff(id1, id2) {
  const len = Math.min(id1.length, id2.length)

  let diffs = 0;
  for (let i = 0; i < len; i++) {
    const c1 = id1.charAt(i);
    const c2 = id2.charAt(i);
    if (c1 !== c2) {
      diffs++;
    }
  }

  return diffs === 1;
}

function sameCharsString(id1, id2) {
  const len = Math.min(id1.length, id2.length)

  let res = "";
  for (let i = 0; i < len; i++) {
    const c1 = id1.charAt(i);
    const c2 = id2.charAt(i);
    if (c1 === c2) {
      res = res + c1;
    }
  }

  return res
}

function main(ids) {
  
  for (let i = 0; i < ids.length; i++) {
    const currentID = ids[i];
    for (let j = i+1; j < ids.length; j++) {
      const diffID = ids[j];

      if (hasOneDiff(currentID, diffID)) {
        console.log(currentID);
        console.log(diffID);

        console.log(sameCharsString(currentID, diffID))
      }
    }
  }
}

const answer = main(input);
console.log(answer);