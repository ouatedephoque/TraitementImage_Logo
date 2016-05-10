import cv2
import numpy as np
import matplotlib.pyplot as plt
from os import listdir
from os.path import isfile, join
import sys

MIN_MATCH_COUNT = 10

# Reprend tous les fichiers dans le dossier /logo/
def fileLogo():
	return [f for f in listdir("logo") if isfile(join("logo", f))]
	
# En-tête commande line
def decorator(str):
	print("**************************************", end="\n\n")
	print("\t%s" % str, end="\n\n")
	print("**************************************", end="\n\n")

# Fonction de recherche du logo
def findLogoMatch(imgToFind):
	nbMatches = 0
	img3 = None
	title = None
	img2 = cv2.imread(imgToFind,0) # trainImage

	listLogo = fileLogo()

	# Parcourir la liste des logos dans le dossier
	for logo in listLogo:
		decorator(logo)
		img1 = cv2.imread('logo\\'+logo,0)          # queryImage

		# Initialiser le détecteur SIFT (ORB était trop brute force et 
		# il faisait cracher le programme)
		sift = cv2.xfeatures2d.SIFT_create()

		# Chercher les points clés sur les images 1 et 2
		kp1, des1 = sift.detectAndCompute(img1,None)
		kp2, des2 = sift.detectAndCompute(img2,None)

		FLANN_INDEX_KDTREE = 0
		index_params = dict(algorithm = FLANN_INDEX_KDTREE, trees = 5)
		search_params = dict(checks = 50)

		# Permet d'avoir les points qui se correspondent entre les deux images
		flann = cv2.FlannBasedMatcher(index_params, search_params)
		matches = flann.knnMatch(des1,des2,k=2)

		# Sauve dans un tableau tous les bons points
		good = []
		for m,n in matches:
			if m.distance < 0.7*n.distance:
				good.append(m)
				
		print("%d points trouvés" % len(good), end="\n\n")
		if len(good) > nbMatches:
			# Longueur minimum de match doit être précisé (évite de faire trop de calcul inutile)
			if len(good)>MIN_MATCH_COUNT:
				"""Point source sur le logo et point de destination sur l'image en paramètre
				   Permet de dessiner les traits vers sur les images avec une destination et 
				   une source"""
				src_pts = np.float32([ kp1[m.queryIdx].pt for m in good ]).reshape(-1,1,2)
				dst_pts = np.float32([ kp2[m.trainIdx].pt for m in good ]).reshape(-1,1,2)

				""" Retourne une Matrice et un mask
					La matrice permet de retrouver le même point dans un autre plan image
					à l'aide d'un calcul"""
				M, mask = cv2.findHomography(src_pts, dst_pts, cv2.RANSAC,5.0)
				
				if mask is not None :
					matchesMask = mask.ravel().tolist()

					h,w = img1.shape
					pts = np.float32([ [0,0],[0,h-1],[w-1,h-1],[w-1,0] ]).reshape(-1,1,2)
					dst = cv2.perspectiveTransform(pts,M)

					img2 = cv2.polylines(img2,[np.int32(dst)],True,255,3, cv2.LINE_AA)
				
					# Paramètre des traits des résultats qu'on a trouvé
					draw_params = dict(matchColor = (0,255,0), # Dessine les correspondances en vert
									   singlePointColor = None,
									   matchesMask = matchesMask,
									   flags = 2)

					# Dessine les traits sur l'image résultat
					img3 = cv2.drawMatches(img1,kp1,img2,kp2,good,None,**draw_params)
					title = logo
				else :
					print("Il y a eu une erreur", end="\n\n")

			else:
				print("Pas assez de points trouvés - %d/%d" % (len(good),MIN_MATCH_COUNT))
				matchesMask = None
				
		decorator("Fin")
			
	return img3,title


if __name__ == "__main__":
	# Vérification si l'utilisateur a bien mis le chemin vers l'image
	if len(sys.argv) < 2 :
		print("Entrer un nombre valide d'argument")
	else :
		imgToFind = sys.argv[1]
		if isfile(imgToFind) is True :
			img3, logo = findLogoMatch(imgToFind)
			title = logo[:-9].title().replace("-", " ")	# Titre au-dessus du graphe

			if img3 is not None:
				plt.title(title)
				plt.imshow(img3, 'gray'),plt.show()
		else :
			print("Entrez un fichier valide")
