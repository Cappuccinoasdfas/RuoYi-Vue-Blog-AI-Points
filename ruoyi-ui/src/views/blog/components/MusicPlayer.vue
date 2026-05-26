<template>
  <div class="music-player glass-card">
    <!-- 播放器头部 -->
    <div class="player-header">
      <h3>
        <i class="el-icon-headset"></i>
        音乐角落
      </h3>
      <el-tooltip content="搜索歌曲" placement="top">
        <div class="search-trigger" @click="showSearch = !showSearch">
          <i class="el-icon-search"></i>
        </div>
      </el-tooltip>
    </div>

    <!-- 搜索框 -->
    <transition name="slide-fade">
      <div v-if="showSearch" class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索歌曲..."
          size="small"
          clearable
          @keyup.enter="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
    </transition>

    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <div class="results-header">
        <span>搜索结果</span>
        <i class="el-icon-close" @click="searchResults = []"></i>
      </div>
      <div class="results-list">
        <div 
          v-for="song in searchResults" 
          :key="song.id"
          class="result-item"
          @click="playSong(song)"
        >
          <img :src="song.pic || defaultCover" :alt="song.name">
          <div class="song-info">
            <div class="song-name">{{ song.name }}</div>
            <div class="song-artist">{{ song.artist }}</div>
          </div>
          <i class="el-icon-video-play"></i>
        </div>
      </div>
    </div>

    <!-- 黑胶唱片区域 -->
    <div class="vinyl-section">
      <div class="vinyl-container" :class="{ playing: isPlaying }">
        <div class="vinyl-disc">
          <img 
            :src="currentSong.pic || defaultCover" 
            :alt="currentSong.name"
            class="cover-image"
          >
          <div class="disc-overlay"></div>
          <div class="disc-center"></div>
        </div>
      </div>
      
      <div class="song-details">
        <div class="song-title">{{ currentSong.name || '未播放' }}</div>
        <div class="song-artist">{{ currentSong.artist || '--' }}</div>
      </div>
    </div>

    <!-- 播放控制区 -->
    <div class="player-controls">
      <!-- 进度条 -->
      <div class="progress-section">
        <span class="time">{{ formatTime(currentTime) }}</span>
        <el-slider
          v-model="currentTime"
          :max="duration"
          :show-tooltip="false"
          @change="handleSeek"
          class="progress-slider"
        ></el-slider>
        <span class="time">{{ formatTime(duration) }}</span>
      </div>

      <!-- 控制按钮 -->
      <div class="control-buttons">
        <div class="control-btn" @click="playPrev" title="上一首">
          <i class="el-icon-d-arrow-left"></i>
        </div>
        <div class="control-btn play-btn" @click="togglePlay" title="播放/暂停">
          <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
        </div>
        <div class="control-btn" @click="playNext" title="下一首">
          <i class="el-icon-d-arrow-right"></i>
        </div>
      </div>

      <!-- 音量控制 -->
      <div class="volume-section">
        <i class="el-icon-microphone"></i>
        <el-slider
          v-model="volume"
          :max="100"
          :show-tooltip="false"
          @change="handleVolumeChange"
          class="volume-slider"
        ></el-slider>
      </div>
    </div>

    <!-- 播放列表 -->
    <div class="playlist-section">
      <div class="playlist-header">
        <span>播放列表 ({{ playlist.length }})</span>
        <el-dropdown trigger="click" @command="handlePlaylistCommand">
          <span class="el-dropdown-link">
            <i class="el-icon-more"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="clear">清空列表</el-dropdown-item>
            <el-dropdown-item command="addDefault">添加默认歌曲</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      
      <div class="playlist">
        <div 
          v-for="(song, index) in playlist" 
          :key="song.id"
          class="playlist-item"
          :class="{ active: currentIndex === index }"
          @click="playSongFromList(index)"
        >
          <img :src="song.pic || defaultCover" :alt="song.name">
          <div class="item-info">
            <div class="item-name">{{ song.name }}</div>
            <div class="item-artist">{{ song.artist }}</div>
          </div>
          <div v-if="currentIndex === index && isPlaying" class="playing-indicator">
            <span></span><span></span><span></span>
          </div>
          <i 
            v-else 
            class="el-icon-close" 
            @click.stop="removeFromPlaylist(index)"
          ></i>
        </div>
      </div>
    </div>

    <!-- 隐藏的 Audio 元素 -->
    <audio 
      ref="audio"
      :src="currentSong.url"
      @timeupdate="onTimeUpdate"
      @loadedmetadata="onLoadedMetadata"
      @ended="onEnded"
      @play="isPlaying = true"
      @pause="isPlaying = false"
    ></audio>
  </div>
</template>

<script>
// import defaultCover from 'https://tse4-mm.cn.bing.net/th/id/OIP-C.Ua23i03ljI1zIYnMvRJrJAHaEF?w=197&h=108&c=7&r=0&o=7&dpr=1.4&pid=1.7&rm=3' // 默认封面图

export default {
  name: 'MusicPlayer',
  data() {
    return {
      defaultCover: 'https://p2.music.126.net/6y-UleOR9wBbQWc1rCjqPw==/109951165771573396.jpg',
      showSearch: false,
      searchKeyword: '',
      searchResults: [],
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      volume: 70,
      currentIndex: 0,
      playlist: [
        // 默认歌曲列表
        {
          id: 1,
          name: '起风了',
          artist: '买辣椒也用券',
          url: 'https://music.163.com/song/media/outer/url?id=1330348068.mp3',
          pic: 'https://p2.music.126.net/diGAyEmpymX8G7JcnElncQ==/109951163699673355.jpg'
        },
      ]
    }
  },
  computed: {
    currentSong() {
      return this.playlist[this.currentIndex] || {}
    }
  },
  mounted() {
    this.initDefaultVolume()
  },
  methods: {
    /**
     * 初始化音量
     */
    initDefaultVolume() {
      this.$nextTick(() => {
        if (this.$refs.audio) {
          this.$refs.audio.volume = this.volume / 100
        }
      })
    },
    
    /**
     * 搜索歌曲
     */
    async handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }
      
      // 这里模拟搜索，实际项目中需要对接音乐API
      // 可以使用网易云音乐API、QQ音乐API等
      this.$message.info('搜索功能需要对接音乐API，这里演示模拟数据')
      
      // 模拟搜索结果
      this.searchResults = [
        {
          id: Date.now(),
          name: this.searchKeyword,
          artist: '未知歌手',
          url: 'https://music.163.com/song/media/outer/url?id=1330348068.mp3',
          pic: defaultCover
        }
      ]
    },
    
    /**
     * 播放歌曲
     */
    playSong(song) {
      // 检查歌曲是否已在列表中
      const exists = this.playlist.findIndex(item => item.id === song.id)
      if (exists === -1) {
        this.playlist.push(song)
        this.currentIndex = this.playlist.length - 1
      } else {
        this.currentIndex = exists
      }
      
      this.searchResults = []
      this.showSearch = false
      this.searchKeyword = ''
      
      this.$nextTick(() => {
        this.$refs.audio.play()
      })
    },
    
    /**
     * 从列表播放
     */
    playSongFromList(index) {
      if (this.currentIndex === index && this.isPlaying) {
        this.$refs.audio.pause()
      } else {
        this.currentIndex = index
        this.$nextTick(() => {
          this.$refs.audio.play()
        })
      }
    },
    
    /**
     * 播放/暂停切换
     */
    togglePlay() {
      if (!this.currentSong.url) {
        this.$message.warning('请先选择歌曲')
        return
      }
      
      if (this.isPlaying) {
        this.$refs.audio.pause()
      } else {
        this.$refs.audio.play().catch(err => {
          console.error('播放失败:', err)
          this.$message.error('播放失败，请检查网络')
        })
      }
    },
    
    /**
     * 上一首
     */
    playPrev() {
      if (this.playlist.length === 0) return
      this.currentIndex = this.currentIndex === 0 
        ? this.playlist.length - 1 
        : this.currentIndex - 1
      this.$nextTick(() => {
        if (this.isPlaying) {
          this.$refs.audio.play()
        }
      })
    },
    
    /**
     * 下一首
     */
    playNext() {
      if (this.playlist.length === 0) return
      this.currentIndex = this.currentIndex === this.playlist.length - 1 
        ? 0 
        : this.currentIndex + 1
      this.$nextTick(() => {
        if (this.isPlaying) {
          this.$refs.audio.play()
        }
      })
    },
    
    /**
     * 播放结束
     */
    onEnded() {
      this.playNext()
    },
    
    /**
     * 时间更新
     */
    onTimeUpdate() {
      this.currentTime = this.$refs.audio.currentTime
    },
    
    /**
     * 加载完成
     */
    onLoadedMetadata() {
      this.duration = this.$refs.audio.duration
    },
    
    /**
     * 进度条拖动
     */
    handleSeek(value) {
      this.$refs.audio.currentTime = value
    },
    
    /**
     * 音量调节
     */
    handleVolumeChange(value) {
      this.$refs.audio.volume = value / 100
    },
    
    /**
     * 格式化时间
     */
    formatTime(seconds) {
      if (!seconds || isNaN(seconds)) return '00:00'
      const mins = Math.floor(seconds / 60)
      const secs = Math.floor(seconds % 60)
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    
    /**
     * 从播放列表移除
     */
    removeFromPlaylist(index) {
      if (this.playlist.length <= 1) {
        this.$message.warning('至少保留一首歌曲')
        return
      }
      
      this.playlist.splice(index, 1)
      if (index < this.currentIndex) {
        this.currentIndex--
      } else if (index === this.currentIndex) {
        this.currentIndex = Math.min(this.currentIndex, this.playlist.length - 1)
      }
    },
    
    /**
     * 播放列表操作
     */
    handlePlaylistCommand(command) {
      switch (command) {
        case 'clear':
          if (this.playlist.length > 0) {
            this.$confirm('确定清空播放列表吗？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.playlist = []
              this.currentIndex = 0
              this.isPlaying = false
              this.$message.success('已清空')
            }).catch(() => {})
          }
          break
        case 'addDefault':
          this.addDefaultSongs()
          break
      }
    },
    
    /**
     * 添加默认歌曲
     */
    addDefaultSongs() {
      const defaultSongs = [
        {
          id: Date.now() + 1,
          name: '晴天',
          artist: '周杰伦',
          url: 'https://music.163.com/song/media/outer/url?id=186016.mp3',
          pic: defaultCover
        }
      ]
      this.playlist.push(...defaultSongs)
      this.$message.success('已添加默认歌曲')
    }
  }
}
</script>

<style lang="scss" scoped>
.music-player {
  padding: 24px 20px;
  color: #fff;
  
  // 播放器头部
  .player-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      
      i {
        color: #ffd700;
      }
    }
    
    .search-trigger {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        background: rgba(255, 255, 255, 0.2);
        transform: scale(1.1);
      }
    }
  }
  
  // 搜索框
  .search-box {
    margin-bottom: 20px;
    
    ::v-deep .el-input-group__append {
      background: rgba(255, 255, 255, 0.15);
      border-color: rgba(255, 255, 255, 0.2);
      
      .el-button {
        background: transparent;
        color: #fff;
      }
    }
  }
  
  // 搜索结果
  .search-results {
    margin-bottom: 20px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: 12px;
    padding: 12px;
    max-height: 200px;
    overflow-y: auto;
    
    .results-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      color: rgba(255, 255, 255, 0.7);
      font-size: 13px;
      
      i {
        cursor: pointer;
        &:hover {
          color: #fff;
        }
      }
    }
    
    .results-list {
      .result-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 8px;
        border-radius: 8px;
        cursor: pointer;
        transition: background 0.3s;
        
        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }
        
        img {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          object-fit: cover;
        }
        
        .song-info {
          flex: 1;
          
          .song-name {
            font-size: 14px;
            font-weight: 500;
          }
          
          .song-artist {
            font-size: 12px;
            opacity: 0.7;
          }
        }
        
        i {
          opacity: 0;
          transition: opacity 0.3s;
        }
        
        &:hover i {
          opacity: 1;
        }
      }
    }
  }
  
  // 黑胶唱片区域
  .vinyl-section {
    text-align: center;
    margin-bottom: 24px;
    
    .vinyl-container {
      width: 200px;
      height: 200px;
      margin: 0 auto 16px;
      position: relative;
      
      &.playing .vinyl-disc {
        animation: rotate 20s linear infinite;
      }
      
      .vinyl-disc {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        position: relative;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        
        .cover-image {
          width: 100%;
          height: 100%;
          border-radius: 50%;
          object-fit: cover;
        }
        
        .disc-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          border-radius: 50%;
          background: radial-gradient(circle at 30% 30%, 
            rgba(255, 255, 255, 0.2) 0%, 
            transparent 50%);
        }
        
        .disc-center {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 30px;
          height: 30px;
          border-radius: 50%;
          background: linear-gradient(135deg, #fff 0%, #ccc 100%);
          border: 3px solid rgba(0, 0, 0, 0.1);
        }
      }
    }
    
    .song-details {
      .song-title {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .song-artist {
        font-size: 14px;
        opacity: 0.8;
      }
    }
  }
  
  // 播放控制区
  .player-controls {
    margin-bottom: 20px;
    
    .progress-section {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 16px;
      
      .time {
        font-size: 12px;
        opacity: 0.8;
        min-width: 40px;
      }
      
      .progress-slider {
        flex: 1;
        
        ::v-deep .el-slider__bar {
          background: linear-gradient(90deg, #ffd700, #ff6b6b);
        }
        
        ::v-deep .el-slider__button {
          border-color: #ffd700;
        }
      }
    }
    
    .control-buttons {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 20px;
      margin-bottom: 16px;
      
      .control-btn {
        width: 45px;
        height: 45px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);
        cursor: pointer;
        transition: all 0.3s;
        
        i {
          font-size: 20px;
        }
        
        &:hover {
          background: rgba(255, 255, 255, 0.2);
          transform: scale(1.1);
        }
        
        &.play-btn {
          width: 60px;
          height: 60px;
          background: linear-gradient(135deg, #ffd700, #ff8c00);
          
          i {
            font-size: 28px;
          }
          
          &:hover {
            box-shadow: 0 0 20px rgba(255, 215, 0, 0.5);
          }
        }
      }
    }
    
    .volume-section {
      display: flex;
      align-items: center;
      gap: 10px;
      
      i {
        font-size: 16px;
        opacity: 0.8;
      }
      
      .volume-slider {
        flex: 1;
        
        ::v-deep .el-slider__bar {
          background: rgba(255, 255, 255, 0.5);
        }
      }
    }
  }
  
  // 播放列表
  .playlist-section {
    .playlist-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 14px;
      font-weight: 500;
      
      .el-dropdown-link {
        cursor: pointer;
        opacity: 0.7;
        
        &:hover {
          opacity: 1;
        }
      }
    }
    
    .playlist {
      max-height: 200px;
      overflow-y: auto;
      
      .playlist-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 8px 10px;
        border-radius: 8px;
        cursor: pointer;
        transition: background 0.3s;
        
        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }
        
        &.active {
          background: rgba(255, 215, 0, 0.2);
        }
        
        img {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          object-fit: cover;
        }
        
        .item-info {
          flex: 1;
          
          .item-name {
            font-size: 14px;
            font-weight: 500;
          }
          
          .item-artist {
            font-size: 12px;
            opacity: 0.7;
          }
        }
        
        .playing-indicator {
          display: flex;
          gap: 2px;
          
          span {
            width: 3px;
            height: 12px;
            background: #ffd700;
            border-radius: 2px;
            animation: equalizer 1s ease-in-out infinite;
            
            &:nth-child(2) {
              animation-delay: 0.2s;
            }
            
            &:nth-child(3) {
              animation-delay: 0.4s;
            }
          }
        }
        
        i {
          opacity: 0;
          transition: opacity 0.3s;
          
          &:hover {
            color: #ff6b6b;
          }
        }
        
        &:hover i {
          opacity: 1;
        }
      }
    }
  }
}

// 动画
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes equalizer {
  0%, 100% {
    height: 8px;
  }
  50% {
    height: 16px;
  }
}

.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-enter {
  transform: translateY(-10px);
  opacity: 0;
}

// 滚动条美化
.playlist::-webkit-scrollbar,
.search-results::-webkit-scrollbar {
  width: 4px;
}

.playlist::-webkit-scrollbar-thumb,
.search-results::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}
</style>