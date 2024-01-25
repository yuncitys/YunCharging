# YunCharging

#### 介绍
YunCharging智慧充电系统以微信、公众号为C端主要入口，为充电用户提供查桩找桩、设备信息查询、在线支付、充电状态查询、账户信息等服务，具备在线充值、支付、实时到账功能，给充电用户带来更加安全、便捷、贴心的充电体验。


## 行业交流群 

**智慧城市项目交流QQ群**

![输入图片说明](docimage.png)

群已满，请加二群：955562455

**项目合作交流微信**

![输入图片说明](wx2.png)


### 在线文档

**[充电项目系统文档在线地址](https://yuncitys.com/yun_charge_paper/)**

**[停车项目系统文档在线地址](https://yuncitys.com/parking_white_paper/)**



### 公司其它开源

**[智慧充电开源仓库地址](https://gitee.com/yuncitys/YunParking)**



# 充电桩硬件产品

> ​		电单车充电桩硬件产品是保障电单车安全快捷充电的重要设施，适用于单位、住宅小区、商场的自行车停车棚，为电动自行车提供充电电源。采用单片微机技术，操作简捷、性能稳定。充电采用智能语音，按照提示音可自行操作充电。支持扫码、刷卡支付功能， 十路输出每路独立保险丝保护，带有较大电流检测，当充电电流大于设定的较大电流时，切断输出。使用时，先插上电源充电器，扫码或者刷卡支付充电金额后，设备会自动检测充电器是否连接正常，再启动充电。

## 技术特点

- 输入电压：220VAC 50HZ ，待机功耗低于5W；

- 保险丝：250V F10A 5*20（有保险丝座，保险丝可更换）每路独立的保险丝座，独立保护；
- 带语音播报功能；
- 设备需安装物联卡联网后才能使用，支持远程固件升级（OTA）；
- 收费模式：时间计费，功率段计费，电量计费； 
- 单端口可以根据设定的功率来做出相应的开启和停止，服务器可以设置参数；
- 单端口开启和停止，独立保护和电池充满提示；
- 单端口充满自停（默认5W,最大255W）,过载保护（默认600W,最大600W），整机总功率过载保护（默认6000W，最大6000W），服务器可以设置；

## 使用环境



- 使用过程中要防止油、盐、水等异物进入机箱，以免引起内部电路短路烧坏器件；
- 应用场合无无腐蚀性及无强电磁辐射，需要定期查看保养除尘和查看工作状态以及周边环境，及早排查潜在不安全因素；
- 工作环境：  工作温度范围：－10℃～＋40℃；储存温度范围：－20℃～＋70℃； 相对湿度范围：40～98%；

## 设备接线示意图









- 接线顺序，按下图接线完成后，最后接入220V 50HZ，空气漏电开关打开；

![image-20220827161418155](https://s2.loli.net/2022/08/29/QnLgfjcOGxT3AFC.png)

- 接入电源后，主板上LED灯闪烁，语音提示“设备启动中”，10路LED灯同时在闪烁,开始连接网络，然后系统提示“设备启动完毕”进入待机， 10路LED灯跑马灯闪烁，若环境信号弱可能找不到网络，无法完成启动，需要在信号强的环境安装；

## 充电桩型号





| **序号** | **型号**                   | **硬件配置**                                                 |
| -------- | -------------------------- | ------------------------------------------------------------ |
| 1        | 10路LED单扫码充电桩        | 触摸LED显示板（不含弹簧）+刷卡+扫码+10路10A继电器主控板+4G天线+喇叭+空气开关 |
| 2        | 10路LED触摸刷卡扫码充电桩  | 触摸LED显示板+刷卡+扫码+10路10A继电器主控板+4G天线+喇叭+空气开关 |
| 3        | 10路轻触按键刷卡扫码充电桩 | 6位数码管轻触按键板+刷卡板+10路10A继电器主控板+通讯板+4G天线+喇叭+空气开关 |
| 4        | 8+2路大功率扫码刷卡充电桩  | 6位数码管轻触按键板+刷卡板+（8路10A继电器+2路30A继电器主控板）+通讯板+4G天线+喇叭+空气开关 |
| 5        | 20路LED显示扫码刷卡充电桩  | LED轻触按键+刷卡板+通讯板+20路10A继电器主控板+4G天线+喇叭+空气开关 |

### 型号介绍

#### 10路LED单扫码充电桩

>一、付款方式：扫码支付
>
>二、产品特点：
>
>1、带语音播报功能
>
>2、充满自停
>
>3、过载保护、漏电保护
>
>4、机箱温度监控预警
>
>5、4G联网通讯，可远程OTA升级
>
>6、收费方式多种：计时、功率段、电量收费
>
>7、独立后台管理
>
>8、远程主动关闭充电订单
>
>9、待机功耗低：低于5W

#### 10路LED触摸刷卡扫码充电桩

![img](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403290.png)

![img](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403861.png)

> 一、付款方式：扫码支付 / 刷卡支付
>
> 二、产品特点：
>
> 1、带语音播报功能
>
> 2、充满自停
>
> 3、过载保护、漏电保护
>
> 4、机箱温度监控预警
>
> 5、4G联网通讯，可远程OTA升级
>
> 6、收费方式多种：计时、功率段、电量收费
>
> 7、独立后台管理
>
> 8、远程主动关闭充电订单
>
> 9、待机功耗低：低于5W

#### 10路轻触按键刷卡扫码充电桩

> 一、付款方式：扫码支付 / 刷卡支付
>
> 二、产品特点：
>
> 1、带语音播报功能
>
> 2、充满自停
>
> 3、过载保护、漏电保护
>
> 4、机箱温度监控预警
>
> 5、4G联网通讯，可远程OTA升级
>
> 6、收费方式多种：计时、功率段、电量收费
>
> 7、独立后台管理平台
>
> 8、远程主动关闭充电订单
>
> 9、待机功耗低：低于5W
>
> 10、余额显示，订单充电时间显示

#### 8+2路大功率扫码刷卡充电桩

>一、付款方式：扫码支付 / 刷卡支付
>
>二、产品特点：
>
>1、带语音播报功能
>
>2、充满自停
>
>3、过载保护、漏电保护
>
>4、机箱温度监控预警
>
>5、4G联网通讯，可远程OTA升级
>
>6、收费方式多种：计时、功率段、电量收费
>
>7、独立后台管理平台
>
>8、远程主动关闭充电订单
>
>9、待机功耗低：低于5W
>
>10、余额显示，订单充电时间显示
>
>11、第5、6路支持单路3500W功率充电

#### 20路LED显示扫码刷卡充电桩

>一、付款方式：扫码支付 / 刷卡支付
>
>二、产品特点：
>
>1、带语音播报功能
>
>2、充满自停
>
>3、过载保护、漏电保护
>
>4、机箱温度监控预警
>
>5、4G联网通讯，可远程OTA升级
>
>6、收费方式多种：计时、功率段、电量收费
>
>7、独立后台管理平台
>
>8、远程主动关闭充电订单
>
>9、待机功耗低：低于5W

# 充电管理运营平台

> 主要包括：**后台运营系统、小程序两个端**

## 后台运营系统

### 首页

- 运营数据统计概况，用户的注册数量，设备的总数，交易金额，总订单，支付方式，用户分布，月收入统计柱状图表

![image-20220827171654816](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403487.png)

### 用户管理

####  微信用户列表

- 所有微信小程序注册用户列表信息的展示，运营人员也可以在后台进行充值，用户账号禁用启用操作。

![image-20220827163411987](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403841.png)

####  支付宝用户列表

- 所有支付宝小程序注册用户列表信息的展示，运营人员也可以在后台进行充值，用户账号禁用启用操作。

![image-20220827163832112](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403831.png)

### 经营统计

####  经营报表

- 经营数据统计看板，可以直观展示昨日的交易数据情况，交易金额，扫码充值金额，IC卡代充金额，套餐预充金额；总用电量，订单总数，可提现的金额，在线设备的总数；
- 代理商、设备不同维度展示订单总数，刷卡、扫码收入总数；

![image-20220827163948641](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404303.png)

###  设备管理

####  未生产设备

- 未生产的设备管理

![image-20220827164016084](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091403215.png)

##### 设备控制

- 设备远程控制，配合现场施工接入调试

![image-20220827164829255](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404188.png)

##### 设置收费方案

- 配置收费的方案，可以根据不同收费类型进行收费，包括，按时间，按电量，免费，按功率，并选择提前配置好的收费方案为每个设备可以设置不同收费方案；

![image-20220827164159235](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404859.png)

##### 小程序二维码生成打印

- 给每个充电桩设备的每个充电口生成二维码，需要打印出来贴到对应的该设备号不同充电口上，用户通过扫码进行充电；

![image-20220827164243739](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404481.png)

##### 设备详情

- 可以查房每个充电桩设备的详情，详细的实时总功率，功率下限，功率上限，总功率上限，警告温度，低温，高温，设备机箱温度，设备等待时间，设备心跳时间，设备信号；

![image-20220827164330598](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404694.png)

####  已入库设备

- 对已生产完的设备可以流转到已入库列表集中进行管理

![image-20220827172853564](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404158.png)

####  未安装设备

- 未安装设备的管理![image-20220827173102605](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091404344.png)

####  已安装设备

- 已经安装的设备，可以并通过设备的收费规则类型：计时、电量、免费分类管理，可以批量进行分配到不同的代理商网点下，批量进行收费规则的配置，同步设备最新的状态；
- 通过操作可以对设备进行远程控制，分配，禁用，删除，收费方案配置，详细查看，小程序二维码生成打印；

![image-20220827164741365](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405417.png)

##### 远程控制

- 远程控制：对设备远程进行调试，可辅助现场进行运维管理；
- 远程启动充电口，继续充电，启动全部端，停止端口，查询端口，刷新端的状态，重启整个充电设备；

![image-20220827173357633](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405712.png)

####  升级软件列表

- 设备远程OTA升级，远程维护更新最新程序；

![image-20220827164937031](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405260.png)

####  设备类型

- 设备类型的管理维护

![image-20220827164955160](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091411444.png)

###  订单管理

####  扫码订单列表

- 通过扫充电桩不充电口进行充电的创单进行管理，并可以查询充电的功率图详情；订单的结束，删除；

![image-20220827165041876](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405010.png)

####  刷卡订单列表

- 通过刷IC卡进行充电的订单列表，下图是充电的详细的功率图，及订单的详细信息；

![image-20220827165132217](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405195.png)

####  免费订单列表

- 免费订单的列表信息管理

![image-20220827165152482](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405989.png)

###  指令管理

####  指令列表

- 设备远程操作，运程控制相关下行指令管理；

![image-20220827165209432](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405897.png)

###  广告管理

####  广告列表

- 通过后台广告投放，可以对应展示到小程序的广告位上，进行活动的推广及运营使用；

![image-20220827165230286](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405870.png)

###  充电站管理

####  充电站列表

- 充电站的管理，创建电创建，修改，删除；

![image-20220827165345159](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405268.png)

###  收费方案

####  计时收费方案

- 按时计费配置收费方案

![image-20220827165439633](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405439.png)

####  电量收费方案

- 按电量收费方案的配置

![image-20220827165526969](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405566.png)

####  功率收费方案

- 按功率收费方案的配置

![image-20220827165554355](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091411595.png)

####  充值套餐方案

- 充值赠送活动配置

![image-20220827165618071](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091405661.png)

###  充值方案管理

####  余额充值方案

- 余额充电方案设置

![image-20220827165639549](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406773.png)

####  IC卡充值方案

- IC卡充值方案

![image-20220827165705475](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406849.png)

###  充电卡管理

####  充电卡列表

- 充电卡管理，可对每个充电卡进行后台充值进行充值，添加充电卡，充电卡的挂失；

![image-20220827165746366](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406588.png)

###  财务管理

####  提现管理

![image-20220827165837483](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406436.png)

####  充值记录

![image-20220827165932127](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406471.png)

###  商品管理

####  商品列表

- 通过商品列表可以发布商品信息，用户在小程序可以购买商品；

![image-20220827170008863](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406133.png)

####  商品上架

- 商品上架

![image-20220827170050538](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406046.png)

####  商品订单

- 商品订单信息，用户下下单的商品信息；对商品发货后填写物流单号进行管理；

![image-20220827170120565](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091406300.png)

![image-20220827175406829](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407743.png)

###  故障管理

####  故障列表

- 用户反馈的设备异常故障信息，后台运营客服可以进行问题处理；

![image-20220827170212406](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407465.png)

###  运营商管理

####  运营商列表

- 支持二级代理商管理，对代理商账号进行开建，分成的利润，账号冻结，初始密码等维护；

![image-20220827170308856](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407218.png)

- 下级代理商管理![image-20220827170459186](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407112.png)

###  权限管理

####  角色管理

- 设备角色管理，不同权限用户可以创建不同角色。根据不同角色可以实现不同级别的数据权限控制；

![image-20220827170527921](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407008.png)

####  菜单管理

- 后台所有菜单配置，菜单功能按钮的管理，权限分配；

![image-20220827170612553](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407091.png)

###  配置管理

####  系统配置

- 针对用户单次充值允许的体现阈值进行全局控制；

![image-20220827170641080](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407734.png)



## 用户小程序

- 微信小程序
- 支付宝小程序

### 首页

- 充电地图，充电桩分布，搜索，附近充电桩查询，扫码充电；

![image-20220827180142647](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407686.png)

#### 附近充电桩

![image-20220827180211267](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091407081.png)

### 我的

- 我的：栏里包含，充电记录查看，钱包充值管理，我要充值进行本人充值及帮他人代充值；用户使用帮助手册，使用故障报修到平台；
- 中心商城：查看商品列表，用户可以进行商品在线购买，购买后订单记录，收费地址管理；
- 代理商：提供给代理商或项目业主直接小程序上登陆后台，可以在项目安装初期对设备进行调试管理，以及运营数据查看；

![image-20220827180327515](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408021.png)

#### 我要充值

![image-20220827180358042](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408572.png)

#### 代人充值

![image-20220827181115645](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408300.png)

#### 商城列表

![image-20220827180428993](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408201.png)

##### 商品信息

![image-20220827180449794](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091410873.png)

##### 商品详情



![image-20220827180526855](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408283.png)

##### 收费地址添加



![image-20220827180636829](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408088.png)

##### 订单列表



![image-20220827180700542](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408217.png)

#### 代理商登陆

- 可以登陆代理商后台，对设备进行施工维护，运营数据查看。

![image-20220827180717807](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408121.png)

##### 代理后台

![image-20220828164005380](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408311.png)

##### 名下代理商



![image-20220828183024345](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408165.png)

##### 用户充值方案设置

![image-20220828183228498](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408607.png)



##### 故障反馈

![image-20220828183311268](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408068.png)

##### IC充电卡充值



![image-20220828183406640](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091408754.png)

##### 安装设备

![image-20220828183453597](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091409938.png)

##### 入库扫码调试

![image-20220828183528685](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091409865.png)

##### 调试入库

![image-20220828185115614](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091409441.png)

##### 设备收费方案

![image-20220828183651472](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091409215.png)

#### 使用帮助

![image-20220827180809168](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091409232.png)



#### 故障报修

![image-20220827180756868](https://raw.githubusercontent.com/caoyingde/SmartCloudWhitePaper/master/img/202209091410386.png)
