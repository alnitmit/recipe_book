import CategoryOutlinedIcon from '@mui/icons-material/CategoryOutlined';
import LocalOfferOutlinedIcon from '@mui/icons-material/LocalOfferOutlined';
import MenuBookOutlinedIcon from '@mui/icons-material/MenuBookOutlined';
import PeopleOutlineOutlinedIcon from '@mui/icons-material/PeopleOutlineOutlined';
import RestaurantOutlinedIcon from '@mui/icons-material/RestaurantOutlined';
import StraightenOutlinedIcon from '@mui/icons-material/StraightenOutlined';
import { Typography } from '@mui/material';
import { NavLink } from 'react-router-dom';

import { Path } from '@/common/routing/index.ts';
import styles from '@/widgets/sidebar/Sidebar.module.css';

const navigation = [
  { to: Path.Recipes, label: 'Рецепты', icon: <MenuBookOutlinedIcon fontSize="small" /> },
  { to: Path.Ingredients, label: 'Ингредиенты', icon: <RestaurantOutlinedIcon fontSize="small" /> },
  { to: Path.Categories, label: 'Категории', icon: <CategoryOutlinedIcon fontSize="small" /> },
  { to: Path.Tags, label: 'Теги', icon: <LocalOfferOutlinedIcon fontSize="small" /> },
  { to: Path.Units, label: 'Единицы', icon: <StraightenOutlinedIcon fontSize="small" /> },
  { to: Path.Users, label: 'Пользователи', icon: <PeopleOutlineOutlinedIcon fontSize="small" /> },
];

export const Sidebar = () => {
  return (
    <aside className={styles.sidebar}>
      <div className={styles.brand}>
        <Typography variant="h5">Recipe Book</Typography>
      </div>
      <nav className={styles.nav}>
        {navigation.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `${styles.link} ${isActive ? styles.active : ''}`.trim()}
          >
            {item.icon}
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
};
