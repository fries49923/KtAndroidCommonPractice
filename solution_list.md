# [Kotlin Android Common Tested Code]

## [FusedLocationTest](./FusedLocationTest)

#Location #FusedLocation #GPS #授權視窗

測試使用 com.google.android.gms 套件中的 FusedLocationProviderClient，透過手機定位功能取得並顯示 經緯度、海拔高度、水平精度、速度與航向角度。同時示範簡易的 權限判斷流程，以及在需要定位權限時彈出的視窗提示

<br>

## [HiltDiTest](./HiltDiTest)

#dependency injection #DI #Dagger Hilt #hiltViewModel

測試 Dagger Hilt，在 Android 專案中使用依賴注入

<br>

## [MmkvTest](./MmkvTest)

#MMKV #KeyValueStorage

MMKV 是高效的記憶體映射式 Key-Value 儲存庫，支援快速存取本地資料；
在 Android App 中測試使用 MMKV 來儲存和讀取 String、Int、enum 等資料

<br>

## [RoomSqliteTest](./RoomSqliteTest)

#Room #SQLite

在 Android App 中測試使用 Room (SQLite)，建立兩張資料表並設定外鍵關係；資料存取透過同步與非同步（Rx）實作，部分方法於單元測試（unitTest）中簡單驗證

<br>

