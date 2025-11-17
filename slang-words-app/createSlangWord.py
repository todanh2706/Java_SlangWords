# !!!!!!!!!!!! Script to generate 100.000 slangs. Consults other classmate !!!!!!!!!!!!

import random
import string
from pathlib import Path

# ---------- settings ----------
OUTPUT_PATH = Path("slang_dataset.txt")
TOTAL_LINES = 100_000
RANDOM_SEED = 42  # change/remove for different results
# -----------------------------

random.seed(RANDOM_SEED)

# Seed data from your examples (exactly as given)
seed_data = r"""#1`Number one
$`Dollar| money
$_$`Has money
%)`Drunk| giddy
&`And
&APOS;S`Wrongly displayed 's
><`Frustration
>,<`Angry| annoyed
>.>`Suspicious| wary
>.<`Frustrated| angry| upset| in pain
>//<`Embarrassed| blushing
>3`Evil but happy|Hate
>:(`Angry
>:)`Evil Grin
>:D`Scheming| Maniacal
>:D<`Hug
>:O`Angry
>O<`Yelling
>_>`Looking left| shifty look
>_<`Frustrated
&HEARTS;`HTML code for a heart
<#`Misspelling of <3
<>`Not equal| No comment
<><`Fish
<.<`Shifty look
</3`Broken Hearted
<2`Not quite love (<3)
<3`Love (heart shape)
<333`Multiple hearts
<3U`Love You
<4`More than love (<3)
<B`Hate
<G>`Grin
<_<`Sarcasm| look left
'EM`Them
(!)`Sarcasm
('_')`Emoticon Representing Boredom
(.)(.)`Female Breasts
(8)`Musical note (MSN)
(:`Happy
(A)`Angel (MSN)
(H)`Cool dude emoticon (MSN)
(K)`Kiss (MSN)
(L)`Love heart (MSN)
(N)`Thumbs down (MSN)
(S)`Seriously
(U)`Heartbroken
(Y)`Thumbs up (MSN)
(YN)`Fingers crossed (MSN)
(^^^)`Shark
(_8^(I)`Homer Simpson
({)(})`Two People Hugging
*`Indicating a spelling correction
*$`Starbucks
*$$`Starbucks
*<:-)`Clown
*.*`Every file on a computer
*G*`Grin
*HUG*`A Cyberhug
*O*`Confused| surprised| shocked
*S*`Salute
*\O/*`Cheerleader
*_*`In love| dazed
+1`Partner |Me too
+REP`Boost reputation
+_+`Dead man
-.-`Annoyance
-_-`Annoyed| tired
.-.`Sad| unhappy
...`Silence| A Trailing Thought
.BIZ`Top level domain for business
.CO`Alternative to .COM
.COM`commerce
.EDU`education
.GOV`governments
.MIL`military
.NET`network providers
.ORG`non-profit organizations
._.`Emoticon Representing Apathy
/.\`Shy| Ashamed
//`Love| I love you
/:)`Flirtation |Sign Of Suspicion
/B/`4chan's random image board
/O/`Happy| arms in the air
/O\`Frustrated| hands on head
/R/`Requesting
/S`Sarcasm
/THREAD`End of a thread on a forum
/W`Whisper
0.0`Surprise
0.O`Raised eyebrow
0/`Waving
0_0`Shocked
10-1`Radio code for 'Receiving poorly'
10-2`Radio code for 'Receiving well'
10-4`Understood, OK
10CHAR`Padding where at least 10 characters are required
10Q`Thank you
10X`Thanks
1AAT`One At A Time
1D`One Direction (band)
1UP`Extra life in a game
2`To
.22`A handgun
24/7`Twenty-four hours a day, seven days a week
2B`To Be
2BH`To Be Honest
2DAY`Today
2EZ`Too Easy
2G`Too Good
2G2BT`Too Good To Be True
2K10`2010
2K4U`To Cool For You
2K9`2009
2L8`Too late
2M`Tomorrow
2MI`Too Much Information
2MM`Tomorrow
2MORO`Tomorrow
2MOZ`Tomorrow
2MRO`Tomorrow
2MRW`Tomorrow
2NITE`Tonight
2NYT`Tonight
2U`To You
2U2`To You Too
3>`Hate
30STM`30 Seconds To Mars (band)
3DG`Three Days Grace (band)
3G`Third Generation mobile phone network
3Q`Thank You
4/20`April 20th| WEED smoking day
401K`Retirement investment plan
.44`.44 Magnum
4CHAN`Internet message/image board
4COL`For Crying Out Loud
4E4`Forever
4EVA`Forever
4EVER`Forever
4G`4th Generation
4GET`Forget
4GM`Forgive me
4M`Forum
4RL?`For Real?
4SHO`For Sure
4SRS`For Serious?
4TW`For The Win
4U`For You
4WD`Four-Wheel Drive
4X4`Four wheel drive vehicle
4YEO`For Your Eyes Only
5 BY 5`Loud and clear, fine
5 HOLE`Gap between goalie's legs in hockey
5EVER`Because Forever Isn't Long Enough| Longer Than 4ever"""

# Parse seed into (slang, defs) pairs
seed_pairs = []
seen_slang = set()
for line in seed_data.splitlines():
    line = line.strip()
    if not line:
        continue
    if "`" not in line:
        continue
    slang, defs = line.split("`", 1)
    if slang and slang not in seen_slang:
        seed_pairs.append((slang, defs))
        seen_slang.add(slang)

# Build a pool of words/phrases for randomized definitions
definition_pool = set()
for _, defs in seed_pairs:
    for part in defs.split("|"):
        w = part.strip()
        if w:
            definition_pool.add(w)

# Add some extra general-purpose words to diversify definitions
definition_pool.update({
    "awkward", "amused", "banter", "best friend", "big mood", "brb",
    "cancelled", "chill", "cringe", "deadpan", "deep cut", "DM me",
    "down bad", "epic", "extra", "facts", "feels", "flex", "glow up",
    "goals", "gutted", "hype", "IRL", "jokes", "lowkey", "meme",
    "mid", "no cap", "omg", "one-liner", "ratio", "receipts",
    "salty", "savage", "simp", "slaps", "sneaky", "soft launch",
    "spill tea", "stan", "sus", "throw shade", "vibes", "win",
    "yikes", "yeet", "zoned out"
})

definition_pool = sorted(definition_pool)

# Characters we can use to synthesize slang terms (excluding the backtick delimiter)
punct = "~!@#$%^&*()-_=+[]{}\\|;:'\",.<>/?"
emojiish = [":)", ":(", ":D", ":P", ";)", ":'(", "xD", "XD", ">_<", ">_>", "<_<", ">:)"]
emote_bits = ["<3", "</3", "o_o", "O_O", "0_0", "^-^", "(y)", "(n)", "(A)", "(H)", "(K)"]
base_chars = string.ascii_letters + string.digits + punct.replace("`", "")

def random_slang():
    """Create a slang key that looks like real chat glyphs but never includes the backtick."""
    # 20% chance: stitch together two small chunks + a decorative bit
    if random.random() < 0.2:
        part1 = "".join(random.choices(base_chars, k=random.randint(1, 3)))
        part2 = random.choice(emojiish + emote_bits)
        part3 = "".join(random.choices(base_chars, k=random.randint(0, 2)))
        return part1 + part2 + part3
    # Otherwise: random length token
    k = random.randint(1, 8)
    token = "".join(random.choices(base_chars, k=k))
    # small chance to sprinkle slashes or angle brackets
    if random.random() < 0.15:
        token = random.choice(["/", ">", "<", "*", "+"]) + token + random.choice(["", "/", ">", "<"])
    return token

def random_defs():
    """Pick 1–4 definitions and join by |, avoiding trivial duplicates."""
    n = random.choices([1,2,3,4], weights=[45,30,20,5], k=1)[0]
    parts = random.sample(definition_pool, k=n)
    return "|".join(parts)

# Assemble lines ensuring unique slang keys
lines = []
lines.extend(f"{s}`{d}" for s, d in seed_pairs)

while len(lines) < TOTAL_LINES:
    s = random_slang()
    if ("`" in s) or (s in seen_slang) or (not s.strip()):
        continue
    d = random_defs()
    lines.append(f"{s}`{d}")
    seen_slang.add(s)

# Write to file
OUTPUT_PATH.write_text("\n".join(lines), encoding="utf-8")
print(f"Wrote {len(lines):,} lines to {OUTPUT_PATH.resolve()}")
