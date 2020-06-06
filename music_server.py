from flask import Flask

app = Flask(__name__)

# 展示歌曲列表
@app.route('/musicIndex')
def music_index():
    return str([{'musicName':'firstSong'}, {'musicName':'secondSong'}])

# 歌曲列表中每首歌曲名对应一个接口, 返回曲谱
@app.route('/firstSong')
# 泡沫
def first_song():
    return ('3a3b3a3b2a2a4b5a2a2a2b2b2a1a3a3a5b6a3a3a3b3a3b2a2a3a4b5a2a2a2b2b2b1b3a' \
        '1a1b3a5b6a6a6b5a6b5a2a2a2b1a2b3a3a3b2a3b2a1a' \
            '1a1b2a3b4a4a4b3a4b3a2a2a2b1a2b1a' \
            '3a3b3b3a2b2b3a4b5a2a2a2b2a2b1b3a3a5b6a3a' \
                '3a3b3a3b2a2a3a4b5a2a2a2b2a2b1a3a' \
                    '1a1b3a5b6a6a6b5a6b5a2a2b1a2b3a3a3b2a3b2a1a1a1b2a3b4a4a4b3a4b3a2a2b1a2b3a')

# 测试
@app.route('/secondSong')
def second_song():
    return '2a3b4a'

app.run(port=5000, host='0.0.0.0', debug=True)
